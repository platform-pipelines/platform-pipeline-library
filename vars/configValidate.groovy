// Returns a list of problems. Collects every error rather than throwing on
// the first, so a misconfigured repo is fixed in one pass instead of five
// failed builds.
//
// Usage:
//   def problems = configValidate(cfg)
// Params: cfg (Map) - fully merged config to validate (see configLoad)
// Returns: List of human-readable problem strings; empty when cfg is valid
def call(Map cfg) {
    def errors = []
    def supported = configSupportedTools()

    if (!cfg.appName)   { errors << 'appName is required' }
    if (!cfg.buildTool) { errors << 'buildTool is required' }

    if (cfg.buildTool && !(cfg.buildTool in supported)) {
        errors << "buildTool '${cfg.buildTool}' unsupported (use: ${supported.join(', ')})"
    }
    if (cfg.containerize && !cfg.imageRepo) {
        errors << 'imageRepo is required when containerize is true'
    }

    def builders = ['kaniko-docker', 'kaniko-k8s', 'buildah']
    if (cfg.containerize && !(cfg.imageBuilder in builders)) {
        errors << "imageBuilder '${cfg.imageBuilder}' unsupported (use: ${builders.join(', ')})"
    }
    if (cfg.quality.signImage && !cfg.containerize) {
        errors << 'quality.signImage requires containerize: true'
    }
    def strategies = ['gitops', 'terraform', 'cloudformation']
    if (cfg.deployStrategy && !(cfg.deployStrategy in strategies)) {
        errors << "deployStrategy '${cfg.deployStrategy}' unsupported (use: ${strategies.join(', ')})"
    }

    if (cfg.environments && cfg.deployStrategy == 'gitops' && !cfg.gitopsRepo) {
        errors << 'gitopsRepo is required when environments are declared with deployStrategy: gitops'
    }

    if (cfg.buildTool in configInfraTools() && cfg.containerize) {
        errors << "containerize must be false for ${cfg.buildTool}"
    }

    // buildTool: cloudformation dispatches cfnBuild/cfnPackage independently
    // of deployStrategy, and both paths call withAwsCredentials, so either
    // one needing infra.region is enough to require it.
    if ((cfg.deployStrategy == 'cloudformation' || cfg.buildTool == 'cloudformation') && !cfg.infra.region) {
        errors << 'infra.region is required for cloudformation'
    }
    if (!(cfg.notify.on in ['always', 'failure', 'change'])) {
        errors << 'notify.on must be always, failure or change'
    }
    if (cfg.quality.minCoverage != null && !(cfg.quality.minCoverage in 0..100)) {
        errors << 'quality.minCoverage must be between 0 and 100'
    }

    def seen = [] as Set
    cfg.environments.eachWithIndex { e, i ->
        if (!e.name) { errors << "environments[${i}].name is required" }

        // manifestPath only means anything for the gitops strategy
        if (cfg.deployStrategy == 'gitops' && !e.manifestPath) {
            errors << "environments[${i}].manifestPath is required for deployStrategy: gitops"
        }
        if (e.requiresApproval && !e.approvers) {
            errors << "environments[${i}] (${e.name}) requires approval but lists no approvers"
        }
        if (e.name && !seen.add(e.name)) {
            errors << "environment '${e.name}' is declared more than once"
        }
    }

    return errors
}
