// Go lint: formatting, vet, then golangci-lint.
// Ordered cheapest-first so a formatting slip fails in seconds.
//
// Usage:
//   goLint(cfg)
// Params: cfg (Map) - pipeline config; cfg.lint.autoFormat/failOnError are read
// Returns: nothing; errors if gofmt/golangci-lint report problems and failOnError is true
def call(Map cfg) {
    logBanner 'Lint: Go'

    if (cfg.lint.autoFormat) {
        sh 'gofmt -w .'
    }

    // gofmt -l prints the files that need formatting. Any output is a failure.
    def unformatted = sh(script: 'gofmt -l .', returnStdout: true).trim()
    if (unformatted) {
        logError "Not gofmt-formatted:\n${unformatted}"
        if (cfg.lint.failOnError) { error 'Run: gofmt -w .' }
    }

    sh 'go vet ./...'

    // golangci-lint v2 writes the report and sets the exit code in one run.
    // The v1 flag --out-format was removed in v2.0; it is --output.<fmt>.path now.
    def status = sh(
        script: 'golangci-lint run --timeout 5m --output.checkstyle.path golangci-report.xml',
        returnStatus: true
    )

    if (status != 0 && cfg.lint.failOnError) {
        error 'golangci-lint reported problems'
    }
}
