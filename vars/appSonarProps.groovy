// Language-specific flags for sonar-scanner.
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
