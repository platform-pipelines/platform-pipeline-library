// Infrastructure deploy: create a change set, gate on it, execute it.
def call(Map cfg, Map envCfg) {
    def changeSet = cfnChangeSet(cfg, envCfg)

    if (!changeSet) {
        logInfo "No changes for ${envCfg.name} — nothing to execute"
        return
    }

    postPlanSummary(cfg, envCfg, env.CFN_CHANGE_SUMMARY)

    if (envCfg.requiresApproval) {
        approvalGate(cfg, envCfg)
    }

    cfnDeploy(cfg, envCfg, changeSet)
}
