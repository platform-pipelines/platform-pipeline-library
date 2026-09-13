// Build output worth archiving and publishing, or null.
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
