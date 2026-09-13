// Reads a single file from any repo without cloning it.
//
// Usage:
//   def yaml = githubFetchFile(repo: 'acme/gitops', path: 'apps/api/values.yaml')
// Params: args.repo (String) - owner/name of the repo; must match owner/name shape
//         args.branch (String) - branch or ref to read from (default 'main')
//         args.path (String) - file path within the repo
// Returns: raw file content as a String; errors if repo is malformed or the request fails
def call(Map args) {
    def slug   = args.repo
    def branch = args.branch ?: 'main'
    def path   = args.path

    if (!(slug =~ /^[\w.-]+\/[\w.-]+$/)) {
        error "githubFetchFile: invalid repo slug '${slug}'"
    }

    def url = "${githubApiUrl()}/repos/${slug}/contents/${path}?ref=${branch}"

    withCredentials([string(credentialsId: githubCredentialsId(), variable: 'GH_TOKEN')]) {
        return sh(
            script: """
                curl -sS --fail \\
                     -H "Authorization: Bearer \$GH_TOKEN" \\
                     -H "Accept: application/vnd.github.raw" \\
                     ${shellQuote(url)}
            """,
            returnStdout: true
        )
    }
}
