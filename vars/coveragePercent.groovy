// Overall line coverage for this repo, or -1 if it cannot be determined.
//
// Usage:
//   def pct = coveragePercent(cfg)
// Params: cfg (Map) - pipeline config; passed to appCoverageFile(cfg) to locate the report
// Returns: line coverage percentage as a BigDecimal, or -1 if no report exists
def call(Map cfg) {
    def report = appCoverageFile(cfg)
    if (!report || !fileExists(report)) { return -1 }

    def script = useScript('coverage_percent.py')
    def out = sh(script: "python3 ${script} ${report}", returnStdout: true).trim()
    return out as BigDecimal
}
