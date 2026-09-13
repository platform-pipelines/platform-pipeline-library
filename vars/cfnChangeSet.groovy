// Creates a change set — CloudFormation's equivalent of a terraform plan.
//
// Returns the change set name, which cfnDeploy then executes. Same principle
// as the Terraform path: what gets executed is the thing that was reviewed,
// not a freshly computed diff.
def call(Map cfg, Map envCfg) {
    logBanner "Change set: ${envCfg.name}"

    def stack = envCfg.stackName ?: "${cfg.appName}-${envCfg.name}"
    def changeSet = "${stack}-${env.BUILD_NUMBER}-${env.GIT_SHORT_SHA}"
    def template = fileExists('packaged-template.yaml') ? 'packaged-template.yaml' : cfg.infra.template

    def params = (envCfg.parameters ?: [:])
        .collect { k, v -> "ParameterKey=${k},ParameterValue=${v}" }.join(' ')
    def paramFlag = params ? "--parameters ${params}" : ''

    def capabilities = (cfg.infra.capabilities ?: []).join(' ')
    def capFlag = capabilities ? "--capabilities ${capabilities}" : ''

    withAwsCredentials(cfg, envCfg) {
        // UPDATE fails if the stack does not exist; CREATE fails if it does.
        def exists = sh(
            script: "aws cloudformation describe-stacks --stack-name ${stack} > /dev/null 2>&1",
            returnStatus: true
        ) == 0

        sh """
            aws cloudformation create-change-set \\
              --stack-name ${stack} \\
              --change-set-name ${changeSet} \\
              --change-set-type ${exists ? 'UPDATE' : 'CREATE'} \\
              --template-body file://${template} \\
              ${paramFlag} ${capFlag} \\
              --tags Key=Version,Value=${env.APP_VERSION} Key=Commit,Value=${env.GIT_COMMIT}
        """

        // Waiting can fail legitimately when the change set is empty, which
        // is a no-op deploy rather than an error.
        def ready = sh(
            script: "aws cloudformation wait change-set-create-complete --stack-name ${stack} --change-set-name ${changeSet}",
            returnStatus: true
        )

        sh "aws cloudformation describe-change-set --stack-name ${stack} --change-set-name ${changeSet} > changeset.json || true"

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
            sh "aws cloudformation delete-change-set --stack-name ${stack} --change-set-name ${changeSet} || true"
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
