// Lint report glob, for surfacing in the build UI.
//
// Usage:
//   def glob = appLintReport(cfg)
// Params: cfg (Map) - pipeline config; only cfg.buildTool is read
// Returns: lint report glob for cfg.buildTool, or null if none applies
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
