// Executes a previously created change set and waits for the stack to settle.
//
// Usage:
//   cfnDeploy(cfg, envCfg, changeSet)
// Params: cfg (Map) - pipeline config; reads cfg.appName
//         envCfg (Map) - target environment config; envCfg.name/stackName
//         changeSet (String) - change set name from cfnChangeSet(); a falsy value is a no-op
// Returns: nothing; errors if the stack does not reach a complete state
def call(Map cfg, Map envCfg, String changeSet) {
    if (!changeSet) {
        logInfo 'No change set — stack already matches the template'
        return
    }

    logBanner "Deploy: ${envCfg.name}"

    def stack = envCfg.stackName ?: "${cfg.appName}-${envCfg.name}"

    withAwsCredentials(cfg, envCfg) {
        sh "aws cloudformation execute-change-set --stack-name ${stack} --change-set-name ${changeSet}"

        def status = sh(
            script: "aws cloudformation wait stack-update-complete --stack-name ${stack} 2>/dev/null || aws cloudformation wait stack-create-complete --stack-name ${stack}",
            returnStatus: true
        )

        if (status != 0) {
            // Surface why it failed rather than making someone open the console.
            sh """
                aws cloudformation describe-stack-events --stack-name ${stack} --max-items 25 \\
                  --query 'StackEvents[?ResourceStatus==`CREATE_FAILED`||ResourceStatus==`UPDATE_FAILED`].[LogicalResourceId,ResourceStatusReason]' \\
                  --output table || true
            """
            error "Stack ${stack} did not reach a complete state"
        }

        sh "aws cloudformation describe-stacks --stack-name ${stack} --query 'Stacks[0].Outputs' --output json > cfn-outputs.json || true"
        archiveArtifacts artifacts: 'cfn-outputs.json', allowEmptyArchive: true
    }

    logAudit('infra.apply', [
        environment: envCfg.name,
        stack      : stack,
        summary    : env.CFN_CHANGE_SUMMARY,
        approver   : env.DEPLOY_APPROVER ?: logActor()
    ])
}
