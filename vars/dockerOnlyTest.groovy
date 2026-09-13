// Usage:
//   dockerOnlyTest(cfg)
// Params: cfg (Map) - pipeline config (unused; kept for dispatcher parity)
def call(Map cfg) { logInfo 'docker-only: no unit tests' }
