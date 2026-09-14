// Blob sha of a file in a repo, or empty string if it does not exist yet.
//
// Usage:
//   def sha = githubFileSha('acme/gitops', 'main', 'apps/api/values.yaml')
// Params: slug (String) - owner/name of the repo; must match owner/name shape
//         branch (String) - branch or ref to look in
//         path (String) - file path within the repo
// Returns: blob sha as a String, or '' if the file does not exist; errors if slug is malformed
def call(String slug, String branch, String path) {
    if (!(slug =~ /^[\w.-]+\/[\w.-]+$/)) {
        error "githubFileSha: invalid repo slug '${slug}'"
    }

    def script = useScript('github_file_sha.py')
    def url = "${githubApiUrl()}/repos/${slug}/contents/${path}?ref=${branch}"

    withCredentials([string(credentialsId: githubCredentialsId(), variable: 'GH_TOKEN')]) {
        return sh(
            script: """
                curl -sS -H "Authorization: Bearer \$GH_TOKEN" \\
                     ${shellQuote(url)} \\
                | python3 ${script}
            """,
            returnStdout: true
        ).trim()
    }
}
