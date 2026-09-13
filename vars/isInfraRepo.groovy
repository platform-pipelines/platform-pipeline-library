// True when this repo describes infrastructure rather than an application.
//
// Usage:
//   if (isInfraRepo(cfg)) { ... }
// Params: cfg (Map) - pipeline config; only cfg.buildTool is read
// Returns: true if cfg.buildTool is one of the infra tools (terraform, cloudformation)
def call(Map cfg) {
    return cfg.buildTool in configInfraTools()
}
