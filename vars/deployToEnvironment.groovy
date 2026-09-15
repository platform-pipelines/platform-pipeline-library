// One environment. Routes on deployStrategy, because deploying an application
// and deploying infrastructure share an approval model but nothing else.
//
// Usage:
//   deployToEnvironment(cfg, envCfg)
// Params: cfg (Map) - pipeline config; cfg.deployStrategy picks the route
//         envCfg (Map) - target environment config
// Returns: nothing; delegates to deployGitops/deployEcs/deployTerraform/deployCloudFormation
def call(Map cfg, Map envCfg) {
    logBanner "Deploy -> ${envCfg.name}"

    switch (cfg.deployStrategy) {
        // Image deploys: the gate comes first. Once the manifest is committed
        // Argo acts on it, and once the service is updated ECS rolls it out.
        case 'gitops':
            if (envCfg.requiresApproval) { approvalGate(cfg, envCfg) }
            deployGitops(cfg, envCfg)
            break
        case 'ecs':
            if (envCfg.requiresApproval) { approvalGate(cfg, envCfg) }
            deployEcs(cfg, envCfg)
            break

        // For infra the gate sits inside, between plan and apply, so the
        // approver sees the actual diff.
        case 'terraform':      deployTerraform(cfg, envCfg);      break
        case 'cloudformation': deployCloudFormation(cfg, envCfg); break

        default:
            error "Unknown deployStrategy '${cfg.deployStrategy}'. Use: ${configDeployStrategies().join(', ')}"
    }
}
