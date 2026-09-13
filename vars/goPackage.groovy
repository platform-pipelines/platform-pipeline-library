// Usage:
//   goPackage(cfg)
// Params: cfg (Map) - pipeline config; cfg.appName is read
// Returns: nothing; builds a stripped static binary into dist/<appName>
def call(Map cfg) {
    logBanner 'Package: Go'
    sh "CGO_ENABLED=0 go build -ldflags '-s -w -X main.version=${env.APP_VERSION}' -o dist/${cfg.appName} ./..."
}
