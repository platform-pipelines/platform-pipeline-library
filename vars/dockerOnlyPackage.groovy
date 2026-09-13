// Usage:
//   dockerOnlyPackage(cfg)
// Params: cfg (Map) - pipeline config (unused; kept for dispatcher parity)
def call(Map cfg) { logInfo 'docker-only: the image is the artifact' }
