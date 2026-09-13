// Nearest semver tag, normalised. Falls back to 0.1.0 for a repo with no tags
// yet so a first build still produces a usable version.
def call() {
    def tag = sh(
        script: 'git describe --tags --abbrev=0 2>/dev/null || echo v0.1.0',
        returnStdout: true
    ).trim()

    def cleaned = tag.startsWith('v') ? tag.substring(1) : tag
    return cleaned ==~ /\d+\.\d+\.\d+/ ? cleaned : '0.1.0'
}
