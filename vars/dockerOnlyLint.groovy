// Nothing to compile, but the Dockerfile itself is worth linting.
//
// Usage:
//   dockerOnlyLint(cfg)
// Params: cfg (Map) - pipeline config; cfg.dockerfile and cfg.lint.failOnError are read
// Returns: nothing; errors if hadolint reports problems and failOnError is true
def call(Map cfg) {
    logBanner 'Lint: Dockerfile'
    // No `|| true` here: it would force the status to 0 and make failOnError a no-op.
    def status = sh(script: "hadolint ${cfg.dockerfile}", returnStatus: true)
    if (status != 0 && cfg.lint.failOnError) {
        error 'hadolint reported problems'
    }
}
