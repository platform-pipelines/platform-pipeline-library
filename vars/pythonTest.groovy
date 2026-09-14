// Usage:
//   pythonTest(cfg)
// Params: cfg (Map) - pipeline config; unused here, kept for call() signature parity across build tools
// Returns: nothing; runs pytest with coverage and JUnit XML output
def call(Map cfg) {
    logBanner 'Test: Python'
    sh 'pytest --junitxml=test-results.xml --cov=. --cov-report=xml --cov-report=term'
}
