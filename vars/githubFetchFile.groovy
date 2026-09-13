// Reads a single file from any repo without cloning it.
def call(Map args) {
    def slug   = args.repo
    def branch = args.branch ?: 'main'
    def path   = args.path

    withCredentials([string(credentialsId: githubCredentialsId(), variable: 'GH_TOKEN')]) {
        return sh(
            script: """
                curl -sS --fail \\
                     -H "Authorization: Bearer \$GH_TOKEN" \\
                     -H "Accept: application/vnd.github.raw" \\
                     "${githubApiUrl()}/repos/${slug}/contents/${path}?ref=${branch}"
            """,
            returnStdout: true
        )
    }
}
