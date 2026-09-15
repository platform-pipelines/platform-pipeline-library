// Usage:
//   pythonTest(cfg)
// Params: cfg (Map) - pipeline config (passed through to pythonVenv)
// Returns: nothing; runs pytest (from requirements-dev.txt) with coverage and JUnit XML output
def call(Map cfg) {
    logBanner 'Test: Python'
    def bin = pythonVenv(cfg)
    sh "${bin}/python -m pytest --junitxml=test-results.xml --cov=. --cov-report=xml --cov-report=term"
}
