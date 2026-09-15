// Builds one static binary into dist/<appName>.
//
// Builds ./cmd/<appName> when that directory exists (the usual layout for a
// repo with several commands), otherwise the module root. Never ./... — with
// -o pointing at a file, `go build ./...` fails as soon as the module has more
// than one package.
//
// Usage:
//   goPackage(cfg)
// Params: cfg (Map) - pipeline config; cfg.appName is read
// Returns: nothing; builds a stripped static binary into dist/<appName>
def call(Map cfg) {
    logBanner 'Package: Go'
    def pkg = fileExists("cmd/${cfg.appName}") ? "./cmd/${cfg.appName}" : '.'
    sh "CGO_ENABLED=0 go build -trimpath -ldflags '-s -w -X main.version=${env.APP_VERSION}' -o dist/${cfg.appName} ${pkg}"
}
