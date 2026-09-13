// Stamps package.json with APP_VERSION and produces an npm pack tarball.
//
// Usage:
//   nodePackage(cfg)
// Params: cfg (Map) - pipeline config (unused directly; kept for dispatch signature parity)
def call(Map cfg) {
    logBanner 'Package: Node'
    sh "npm version ${env.APP_VERSION} --no-git-tag-version --allow-same-version"
    sh 'npm pack'
}
