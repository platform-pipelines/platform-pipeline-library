// One-line severity tally from a Trivy JSON report, for the audit log.
//
// Usage:
//   def summary = trivySummary('trivy-fs.json')
// Params: jsonReport (String) - path to a Trivy JSON report file
// Returns: one-line severity summary, or 'no-report' if the file doesn't exist
def call(String jsonReport) {
    if (!fileExists(jsonReport)) { return 'no-report' }
    def script = useScript('trivy_summary.py')
    return sh(script: "python3 ${script} ${jsonReport}", returnStdout: true).trim()
}
