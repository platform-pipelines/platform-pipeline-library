// Manual gate before a protected environment.
//
// Two things matter here: the approver is recorded in the audit trail and
// carried into the GitOps commit message, and by default nobody can approve
// their own deploy.
def call(Map cfg, Map envCfg) {
    def requester = logActor()

    logBanner "Approval required: ${envCfg.name}"
    logInfo "Approvers: ${envCfg.approvers.join(', ')}"

    def answer = requestApproval(cfg, envCfg)
    def who    = answer.APPROVER
    def reason = answer.REASON

    if (who == requester && !cfg.extra.allowSelfApproval) {
        logAudit('deploy.self_approval_blocked', [environment: envCfg.name, user: who])
        error "Self-approval blocked: ${who} triggered this build and cannot approve their own deploy to ${envCfg.name}"
    }

    env.DEPLOY_APPROVER = who
    logInfo "Approved by ${who}"
    logAudit('deploy.approved', [environment: envCfg.name, approver: who, requester: requester, reason: reason])
}
