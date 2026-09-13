// Coverage report the gate and Sonar both read, or null.
//
// Usage:
//   def report = appCoverageFile(cfg)
// Params: cfg (Map) - pipeline config; only cfg.buildTool is read
// Returns: coverage report path for cfg.buildTool, or null if none applies
def call(Map cfg) {
    switch (cfg.buildTool) {
        case 'go':     return 'coverage.out'
        case 'python': return 'coverage.xml'
        case 'maven':  return 'target/site/jacoco/jacoco.xml'
        case 'gradle': return 'build/reports/jacoco/test/jacocoTestReport.xml'
        case 'npm':    return 'coverage/lcov.info'
        default:       return null
    }
}
