// Fails the build when coverage is below the configured minimum.
// Skipped entirely when quality.minCoverage is unset.
//
// Usage:
//   checkCoverage(cfg)
// Params: cfg (Map) - pipeline config; reads cfg.quality.minCoverage
// Returns: nothing; sets a GitHub commit status and errors if coverage is below the minimum
def call(Map cfg) {
    def minimum = cfg.quality.minCoverage
    if (minimum == null) { return }

    def actual = coveragePercent(cfg)
    if (actual < 0) {
        logWarn "Could not read coverage from ${appCoverageFile(cfg)} — skipping gate"
        return
    }

    logInfo "Coverage ${actual}% (minimum ${minimum}%)"
    logAudit('quality.coverage', [coverage: actual, minimum: minimum])

    if (actual < minimum) {
        githubSetStatus('ci/coverage', 'failure', "${actual}% < ${minimum}%")
        error "Coverage ${actual}% is below the required ${minimum}%"
    }
    githubSetStatus('ci/coverage', 'success', "${actual}%")
}
