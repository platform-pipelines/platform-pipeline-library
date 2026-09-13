// Infrastructure deploy: plan, gate on the plan, apply that exact plan.
//
// The approval sits between plan and apply deliberately — an approver is
// approving a specific set of changes, not a general intention to deploy.
def call(Map cfg, Map envCfg) {
    def planFile = terraformPlan(cfg, envCfg)

    if (env.TF_HAS_CHANGES != 'true') {
        logInfo "No changes for ${envCfg.name} — skipping apply"
        return
    }

    postPlanSummary(cfg, envCfg, env.TF_PLAN_SUMMARY)

    if (envCfg.requiresApproval) {
        approvalGate(cfg, envCfg)
    }

    terraformApply(cfg, envCfg, planFile)
}
