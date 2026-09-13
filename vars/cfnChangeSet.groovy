// Creates a change set — CloudFormation's equivalent of a terraform plan.
//
// Returns the change set name, which cfnDeploy then executes. Same principle
// as the Terraform path: what gets executed is the thing that was reviewed,
// not a freshly computed diff.
//
// Usage:
//   def changeSet = cfnChangeSet(cfg, envCfg)
// Params: cfg (Map) - pipeline config; reads cfg.appName, cfg.infra.template/capabilities
//         envCfg (Map) - target environment config; envCfg.name/stackName/parameters
// Returns: the created change set name, or null if the stack already matches the template
def call(Map cfg, Map envCfg) {
    logBanner "Change set: ${envCfg.name}"

    def stack = envCfg.stackName ?: "${cfg.appName}-${envCfg.name}"
    def changeSet = "${stack}-${env.BUILD_NUMBER}-${env.GIT_SHORT_SHA}"
    def template = fileExists('packaged-template.yaml') ? 'packaged-template.yaml' : cfg.infra.template

    def keyPattern = ~/^[A-Za-z0-9_-]+$/
    (envCfg.parameters ?: [:]).keySet().each { k ->
        if (!(k ==~ keyPattern)) {
            error "cfnChangeSet: invalid CloudFormation parameter key '${k}'"
        }
    }
    (cfg.infra.capabilities ?: []).each { c ->
        if (!(c ==~ keyPattern)) {
            error "cfnChangeSet: invalid CloudFormation capability '${c}'"
        }
    }

    def paramsFile = 'cfn-params.json'
    if (envCfg.parameters) {
        writeJSON file: paramsFile, json: envCfg.parameters.collect { k, v ->
            [ParameterKey: k, ParameterValue: v as String]
        }
    }
    def paramFlag = envCfg.parameters ? "--parameters file://${paramsFile}" : ''

    def capabilities = (cfg.infra.capabilities ?: []).join(' ')
    def capFlag = capabilities ? "--capabilities ${capabilities}" : ''

    withAwsCredentials(cfg, envCfg) {
        // UPDATE fails if the stack does not exist; CREATE fails if it does.
        def exists = sh(
            script: "aws cloudformation describe-stacks --stack-name ${shellQuote(stack)} > /dev/null 2>&1",
            returnStatus: true
        ) == 0

        sh """
            aws cloudformation create-change-set \\
              --stack-name ${shellQuote(stack)} \\
              --change-set-name ${shellQuote(changeSet)} \\
              --change-set-type ${exists ? 'UPDATE' : 'CREATE'} \\
              --template-body ${shellQuote("file://${template}")} \\
              ${paramFlag} ${capFlag} \\
              --tags Key=Version,Value=${shellQuote(env.APP_VERSION)} Key=Commit,Value=${shellQuote(env.GIT_COMMIT)}
        """

        // Waiting can fail legitimately when the change set is empty, which
        // is a no-op deploy rather than an error.
        def ready = sh(
            script: "aws cloudformation wait change-set-create-complete --stack-name ${shellQuote(stack)} --change-set-name ${shellQuote(changeSet)}",
            returnStatus: true
        )

        sh "aws cloudformation describe-change-set --stack-name ${shellQuote(stack)} --change-set-name ${shellQuote(changeSet)} > changeset.json || true"

        def summary = sh(
            script: "python3 ${useScript('cfn_changeset_summary.py')} changeset.json",
            returnStdout: true
        ).trim()

        env.CFN_CHANGE_SUMMARY = summary
        logInfo "Change set: ${summary}"

        // A FAILED change set means "no changes" only when AWS says so.
        // Any other failure is a real error and must not be mistaken for a
        // successful no-op deploy.
        if (summary.startsWith('failed')) {
            error "Change set ${changeSet} failed: ${summary}"
        }

        if (summary.startsWith('no changes')) {
            sh "aws cloudformation delete-change-set --stack-name ${shellQuote(stack)} --change-set-name ${shellQuote(changeSet)} || true"
            env.CFN_HAS_CHANGES = 'false'
            logInfo 'Stack already matches the template'
            return null
        }

        if (ready != 0) {
            error "Change set ${changeSet} did not create cleanly. See changeset.json"
        }

        env.CFN_HAS_CHANGES = 'true'
        archiveArtifacts artifacts: 'changeset.json', allowEmptyArchive: true
        logAudit('infra.changeset', [environment: envCfg.name, stack: stack, summary: summary])
    }

    return changeSet
}
