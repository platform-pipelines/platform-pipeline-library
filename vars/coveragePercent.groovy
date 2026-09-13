// Overall line coverage for this repo, or -1 if it cannot be determined.
def call(Map cfg) {
    def report = appCoverageFile(cfg)
    if (!report || !fileExists(report)) { return -1 }

    def script = useScript('coverage_percent.py')
    def out = sh(script: "python3 ${script} ${report}", returnStdout: true).trim()
    return out as BigDecimal
}
