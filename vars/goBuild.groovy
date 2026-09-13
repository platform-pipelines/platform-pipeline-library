// Static binary, version stamped into main.version.
//
// Usage:
//   goBuild(cfg)
// Params: cfg (Map) - pipeline config (unused; kept for dispatcher parity)
// Returns: nothing; downloads modules and builds ./...
def call(Map cfg) {
    logBanner 'Build: Go'
    sh 'go mod download'
    sh 'go build ./...'
}
