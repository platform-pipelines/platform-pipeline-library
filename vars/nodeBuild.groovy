// Installs dependencies and runs the build script if the project has one.
//
// Usage:
//   nodeBuild(cfg)
// Params: cfg (Map) - pipeline config, passed through to nodeInstall
def call(Map cfg) {
    logBanner 'Build: Node'
    nodeInstall(cfg)
    sh 'npm run build --if-present'
}
