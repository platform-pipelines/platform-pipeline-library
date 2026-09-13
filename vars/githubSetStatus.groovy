// Publishes a commit status so PR pages show pass/fail inline.
// state: pending | success | failure | error
//
// Usage:
//   githubSetStatus('sonar', 'success', 'Quality gate passed')
// Params: context (String) - status check name shown on the PR
//         state (String) - pending | success | failure | error
//         description (String) - short text, truncated to 140 chars
// Returns: nothing; no-op when env.GIT_COMMIT is unavailable
def call(String context, String state, String description) {
    if (!env.GIT_COMMIT) {
        logDebug 'No GIT_COMMIT — skipping commit status'
        return
    }

    def body = groovy.json.JsonOutput.toJson([
        state      : state,
        context    : context,
        description: description.take(140),
        target_url : env.BUILD_URL ?: '',
    ])

    githubApiRequest(
        method     : 'POST',
        path       : "/repos/${githubRepoSlug()}/statuses/${env.GIT_COMMIT}",
        body       : body,
        description: "status ${context}=${state}"
    )
}
