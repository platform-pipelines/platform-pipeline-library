// Reads .ci/config.yaml, layers it over defaults, applies inline overrides,
// validates, and returns a plain Map.
//
// Unknown keys and deprecated keys are logged as warnings rather than failing
// the build; invalid values fail it, with every problem listed at once.
//
// Usage:
//   def cfg = configLoad()
//   def cfg = configLoad([configFile: '.ci/other.yaml', quality: [minCoverage: 90]])
// Params: overrides (Map) - inline overrides merged in last; 'configFile' key picks the yaml path
// Returns: fully merged and validated config Map; errors if validation finds any problems
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

    // YAML 1.1 (SnakeYAML, which readYaml uses) parses the bare key `on:` as
    // boolean true, so `notify: { on: failure }` arrives as `{true: failure}`
    // and the setting was silently ignored. Put it back under its real name.
    if (raw.notify instanceof Map && raw.notify.containsKey(true)) {
        raw.notify = [:] + raw.notify
        def value = raw.notify.remove(true)
        if (!raw.notify.containsKey('on')) { raw.notify.on = value }
    }

    // What the repo actually wrote, before defaults fill the gaps.
    def explicit = configMerge(raw, opts)

    configUnknownKeys(explicit).each { logWarn "${it} (in ${path})" }

    def cfg = configMerge(configDefaults(), explicit)

    // Moved keys keep working: copy the old value across unless the new key
    // was also written, in which case the new key wins.
    configDeprecatedKeys().each { oldPath, newPath ->
        def o = oldPath.tokenize('.')
        def n = newPath.tokenize('.')
        def oldValue = explicit[o[0]] instanceof Map ? explicit[o[0]][o[1]] : null
        if (oldValue == null) { return }

        def newWritten = explicit[n[0]] instanceof Map && explicit[n[0]][n[1]] != null
        if (newWritten) {
            logWarn "${oldPath} is ignored because ${newPath} is also set — remove ${oldPath}"
        } else {
            cfg[n[0]][n[1]] = oldValue
            logWarn "${oldPath} is deprecated — move it to ${newPath}"
        }
    }

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
