// Runs the project's npm test script.
//
// Installs first when node_modules is missing: every stage starts from the
// source stash, so dependencies installed by the Build stage are not here.
//
// Usage:
//   nodeTest(cfg)
// Params: cfg (Map) - pipeline config (passed through to nodeInstall)
def call(Map cfg) {
    logBanner 'Test: Node'
    if (!fileExists('node_modules')) {
        nodeInstall(cfg)
    }
    sh 'npm test'
}
