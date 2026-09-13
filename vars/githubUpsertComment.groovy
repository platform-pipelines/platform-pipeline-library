// Edits the previous bot comment carrying the same marker instead of adding
// another. Without this, a busy PR collects twenty identical scan reports.
//
// Usage:
//   githubUpsertComment('<!-- sonar-report -->', reportMarkdown)
// Params: marker (String) - hidden marker text identifying the bot comment
//         markdown (String) - comment body, appended after the marker
// Returns: nothing; no-op outside PR builds (no env.CHANGE_ID)
def call(String marker, String markdown) {
    if (!env.CHANGE_ID) { return }

    def existing = githubFindComment(marker)
    def body = groovy.json.JsonOutput.toJson([body: "${marker}\n\n${markdown}"])

    if (existing) {
        githubApiRequest(
            method     : 'PATCH',
            path       : "/repos/${githubRepoSlug()}/issues/comments/${existing}",
            body       : body,
            description: 'update PR comment'
        )
    } else {
        githubApiRequest(
            method     : 'POST',
            path       : "/repos/${githubRepoSlug()}/issues/${env.CHANGE_ID}/comments",
            body       : body,
            description: 'new PR comment'
        )
    }
}
