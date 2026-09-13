// Runs the project's npm test script.
//
// Usage:
//   nodeTest(cfg)
// Params: cfg (Map) - pipeline config (unused directly; kept for dispatch signature parity)
def call(Map cfg) {
    logBanner 'Test: Node'
    sh 'npm test'
}
