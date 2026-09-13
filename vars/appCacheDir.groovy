// Directory worth persisting between builds, or null.
def call(Map cfg) {
    switch (cfg.buildTool) {
        case 'go':     return '.gocache'
        case 'python': return '.pip-cache'
        case 'maven':  return '.m2'
        case 'gradle': return '.gradle'
        case 'npm':    return 'node_modules'
        case 'terraform':      return '.terraform'
        default:       return null
    }
}
