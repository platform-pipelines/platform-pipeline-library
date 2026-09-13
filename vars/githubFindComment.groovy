// Id of the existing bot comment carrying this marker, or empty string.
def call(String marker) {
    def script = useScript('find_pr_comment.py')

    withCredentials([string(credentialsId: githubCredentialsId(), variable: 'GH_TOKEN')]) {
        return sh(
            script: """
                curl -sS -H "Authorization: Bearer \$GH_TOKEN" \\
                     -H "Accept: application/vnd.github+json" \\
                     "${githubApiUrl()}/repos/${githubRepoSlug()}/issues/${env.CHANGE_ID}/comments?per_page=100" \\
                | python3 ${script} ${shellQuote(marker)}
            """,
            returnStdout: true
        ).trim()
    }
}
