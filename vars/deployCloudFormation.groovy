// Infrastructure deploy: create a change set, gate on it, execute it.
//
// Usage:
//   deployCloudFormation(cfg, envCfg)
// Params: cfg (Map) - pipeline config
//         envCfg (Map) - target environment config; envCfg.requiresApproval gates on approvalGate
// Returns: nothing; a no-op when the change set has no changes to apply
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
