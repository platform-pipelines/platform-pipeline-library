// Lint report glob, for surfacing in the build UI.
def call(Map cfg) {
    switch (cfg.buildTool) {
        case 'go':     return 'golangci-report.xml'
        case 'python': return 'ruff-report.xml'
        case 'maven':  return 'target/checkstyle-result.xml'
        case 'gradle': return 'build/reports/checkstyle/*.xml'
        case 'npm':    return 'eslint-report.xml'
        case 'terraform':      return 'tflint-report.xml'
        case 'cloudformation': return 'cfn-lint-report.xml'
        default:       return null
    }
}
