// Language-specific flags for sonar-scanner.
//
// Usage:
//   def props = appSonarProps(cfg)
// Params: cfg (Map) - pipeline config; only cfg.buildTool is read
// Returns: Map of sonar-scanner properties for cfg.buildTool, or [:] if none apply
def call(Map cfg) {
    switch (cfg.buildTool) {
        case 'go':
            return ['sonar.go.coverage.reportPaths': 'coverage.out']
        case 'python':
            return ['sonar.python.coverage.reportPaths': 'coverage.xml']
        case 'maven':
            return ['sonar.java.binaries'                 : 'target/classes',
                    'sonar.coverage.jacoco.xmlReportPaths': 'target/site/jacoco/jacoco.xml',
                    'sonar.junit.reportPaths'             : 'target/surefire-reports']
        case 'gradle':
            return ['sonar.java.binaries'                 : 'build/classes',
                    'sonar.coverage.jacoco.xmlReportPaths': 'build/reports/jacoco/test/jacocoTestReport.xml']
        case 'npm':
            return ['sonar.javascript.lcov.reportPaths': 'coverage/lcov.info']
        default:
            return [:]
    }
}
