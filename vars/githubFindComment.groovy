// Id of the existing bot comment carrying this marker, or empty string.
//
// Usage:
//   def id = githubFindComment('<!-- sonar-report -->')
// Params: marker (String) - hidden marker text identifying the bot comment
// Returns: comment id as a String, or '' if no comment carries the marker
def call(String marker) {
    def script = useScript('find_pr_comment.py')
    def url = "${githubApiUrl()}/repos/${githubRepoSlug()}/issues/${env.CHANGE_ID}/comments?per_page=100"

    withCredentials([string(credentialsId: githubCredentialsId(), variable: 'GH_TOKEN')]) {
        return sh(
            script: """
                curl -sS -H "Authorization: Bearer \$GH_TOKEN" \\
                     -H "Accept: application/vnd.github+json" \\
                     ${shellQuote(url)} \\
                | python3 ${script} ${shellQuote(marker)}
            """,
            returnStdout: true
        ).trim()
    }
}
