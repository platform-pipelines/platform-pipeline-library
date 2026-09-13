// Runs a build body in the right place for this agent.
//
// On a toolbox agent the tools are already present, so the body runs directly
// — wrapping it in a container would need a docker socket and defeat the
// purpose. On a plain agent it falls back to a per-language image.
//
// Usage:
//   inBuildContainer(cfg) { sh 'go build ./...' }
// Params: cfg (Map) - pipeline config; determines the per-language image/cache dir
//         body (Closure) - build steps to run
// Returns: nothing; runs body() in place or inside the right container
def call(Map cfg, Closure body) {
    if (usingToolbox()) {
        logDebug 'Toolbox agent — running in place'
        body()
    } else {
        inContainer(appToolImage(cfg), appCacheDir(cfg), body)
    }
}
