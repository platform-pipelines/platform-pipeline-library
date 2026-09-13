// The input() call, with rejection and timeout distinguished so the audit log
// records which one happened.
def call(Map cfg, Map envCfg) {
    try {
        timeout(time: envCfg.approvalTimeoutMinutes, unit: 'MINUTES') {
            return input(
                id                : "deploy-${envCfg.name}",
                message           : "Deploy ${cfg.appName} ${env.APP_VERSION} to ${envCfg.name}?",
                ok                : "Deploy to ${envCfg.name}",
                submitter         : envCfg.approvers.join(','),
                submitterParameter: 'APPROVER',
                parameters        : [
                    text(name: 'REASON', defaultValue: '',
                         description: 'Change reference or reason (recorded in the audit log)')
                ]
            )
        }
    } catch (org.jenkinsci.plugins.workflow.steps.FlowInterruptedException e) {
        def cause = e.causes ? e.causes[0] : null
        if (cause instanceof org.jenkinsci.plugins.workflow.support.steps.input.Rejection) {
            logAudit('deploy.rejected', [environment: envCfg.name, by: cause.userId])
            error "Deploy to ${envCfg.name} rejected by ${cause.userId}"
        }
        logAudit('deploy.timeout', [environment: envCfg.name, waited: "${envCfg.approvalTimeoutMinutes}m"])
        error "Approval for ${envCfg.name} timed out after ${envCfg.approvalTimeoutMinutes} minutes"
    }
}
