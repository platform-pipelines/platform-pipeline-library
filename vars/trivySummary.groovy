// One-line severity tally from a Trivy JSON report, for the audit log.
def call(String jsonReport) {
    if (!fileExists(jsonReport)) { return 'no-report' }
    def script = useScript('trivy_summary.py')
    return sh(script: "python3 ${script} ${jsonReport}", returnStdout: true).trim()
}
