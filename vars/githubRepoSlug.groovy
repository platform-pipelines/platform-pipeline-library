// owner/repo derived from the checkout rather than configured a second time
// in config.yaml, where it would eventually drift.
def call() {
    def url = env.GIT_URL ?: sh(script: 'git config --get remote.origin.url', returnStdout: true).trim()
    def m = url =~ /(?:github\.com[:\/])([^\/]+\/[^\/]+?)(?:\.git)?\s*$/
    if (!m) { error "Cannot derive repo slug from remote: ${url}" }
    return m[0][1]
}
