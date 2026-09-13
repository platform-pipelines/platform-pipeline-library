// npm ci, not npm install: it honours the lockfile exactly and fails loudly
// if package.json and the lockfile disagree.
//
// Usage:
//   nodeInstall(cfg)
// Params: cfg (Map) - pipeline config (unused directly; kept for dispatch signature parity)
def call(Map cfg) {
    sh 'npm ci --prefer-offline --no-audit --fund=false'
}
