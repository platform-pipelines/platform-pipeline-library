// Reads .ci/config.yaml, layers it over defaults, applies inline overrides,
// validates, and returns a plain Map.
def call(Map overrides = [:]) {
    def opts = [:] + overrides
    def path = opts.remove('configFile') ?: '.ci/config.yaml'

    def raw = [:]
    if (fileExists(path)) {
        raw = readYaml(file: path) ?: [:]
        logInfo "Loaded pipeline config from ${path}"
    } else {
        logWarn "No ${path} found — using defaults and inline overrides only"
    }

    def cfg = configMerge(configMerge(configDefaults(), raw), opts)

    cfg.environments = (cfg.environments ?: []).collect { e ->
        def merged = configMerge(configEnvDefaults(), (e ?: [:]) as Map)
        merged.namespace = merged.namespace ?: merged.name
        merged
    }

    cfg.quality.sonarProjectKey = cfg.quality.sonarProjectKey ?: cfg.appName

    // Infrastructure repos produce no container image and deploy by applying
    // a plan, so derive both rather than making every IaC repo restate them.
    def isInfra = cfg.buildTool in configInfraTools()
    if (isInfra) {
        cfg.containerize = false
        cfg.deployStrategy = cfg.deployStrategy ?: cfg.buildTool
        cfg.quality.sbom = false
        cfg.quality.signImage = false
    } else {
        cfg.deployStrategy = cfg.deployStrategy ?: 'gitops'
    }

    def problems = configValidate(cfg)
    if (problems) {
        error "Invalid pipeline config (${path}):\n  - " + problems.join('\n  - ')
    }

    return cfg
}
