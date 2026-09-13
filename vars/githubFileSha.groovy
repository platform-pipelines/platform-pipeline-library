// Blob sha of a file in a repo, or empty string if it does not exist yet.
def call(String slug, String branch, String path) {
    def script = useScript('github_file_sha.py')

    withCredentials([string(credentialsId: githubCredentialsId(), variable: 'GH_TOKEN')]) {
        return sh(
            script: """
                curl -sS -H "Authorization: Bearer \$GH_TOKEN" \\
                     "${githubApiUrl()}/repos/${slug}/contents/${path}?ref=${branch}" \\
                | python3 ${script}
            """,
            returnStdout: true
        ).trim()
    }
}
