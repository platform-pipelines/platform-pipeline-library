// Nothing to compile, but the Dockerfile itself is worth linting.
def call(Map cfg) {
    logBanner 'Lint: Dockerfile'
    def status = sh(script: "hadolint ${cfg.dockerfile} || true", returnStatus: true)
    if (status != 0 && cfg.lint.failOnError) {
        error 'hadolint reported problems'
    }
}
