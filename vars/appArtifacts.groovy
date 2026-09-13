// Build output worth archiving and publishing, or null.
//
// Usage:
//   def glob = appArtifacts(cfg)
// Params: cfg (Map) - pipeline config; only cfg.buildTool is read
// Returns: artifact glob for cfg.buildTool, or null if it produces none
def call(Map cfg) {
    switch (cfg.buildTool) {
        case 'go':     return 'dist/*'
        case 'python': return 'dist/*'
        case 'maven':  return 'target/*.jar'
        case 'gradle': return 'build/libs/*.jar'
        case 'npm':    return '*.tgz'
        case 'terraform':      return 'tfplan-*.txt'
        case 'cloudformation': return 'packaged-template.yaml'
        default:       return null
    }
}
