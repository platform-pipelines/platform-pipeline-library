// Directory worth persisting between builds, or null.
//
// Usage:
//   def dir = appCacheDir(cfg)
// Params: cfg (Map) - pipeline config; only cfg.buildTool is read
// Returns: cache directory path for cfg.buildTool, or null if none applies
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
