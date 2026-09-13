// One environment. Routes on deployStrategy, because deploying an application
// and deploying infrastructure share an approval model but nothing else.
//
// Usage:
//   deployToEnvironment(cfg, envCfg)
// Params: cfg (Map) - pipeline config; cfg.deployStrategy picks the route
//         envCfg (Map) - target environment config
// Returns: nothing; delegates to deployGitops/deployTerraform/deployCloudFormation
def call(Map cfg, Map envCfg) {
    logBanner "Deploy -> ${envCfg.name}"

    switch (cfg.deployStrategy) {
        case 'gitops':
            // Approval before the manifest bump: once it is committed, Argo
            // acts on it, so the gate has to come first.
            if (envCfg.requiresApproval) { approvalGate(cfg, envCfg) }
            deployGitops(cfg, envCfg)
            break

        // For infra the gate sits inside, between plan and apply, so the
        // approver sees the actual diff.
        case 'terraform':      deployTerraform(cfg, envCfg);      break
        case 'cloudformation': deployCloudFormation(cfg, envCfg); break

        default:
            error "Unknown deployStrategy '${cfg.deployStrategy}'"
    }
}
