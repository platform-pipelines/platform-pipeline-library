// JUnit XML glob, or null when the toolchain produces none.
def call(Map cfg) {
    switch (cfg.buildTool) {
        case 'go':     return 'test-results.xml'
        case 'python': return 'test-results.xml'
        case 'maven':  return 'target/surefire-reports/*.xml'
        case 'gradle': return 'build/test-results/test/*.xml'
        case 'npm':    return 'junit.xml'
        case 'terraform':      return 'tflint-report.xml'
        case 'cloudformation': return 'cfn-lint-report.xml'
        default:       return null
    }
}
