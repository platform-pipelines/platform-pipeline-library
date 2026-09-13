// Coverage report the gate and Sonar both read, or null.
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
