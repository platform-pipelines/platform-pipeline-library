// Adds a PR comment. No-op outside PR builds.
//
// Not used by standardPipeline — githubUpsertComment is, because it replaces
// rather than accumulates. This one is kept as public API for consumers who
// want a one-off comment from a custom stage in their own Jenkinsfile.
def call(String markdown) {
    if (!env.CHANGE_ID) {
        logDebug 'Not a PR build — skipping comment'
        return
    }
    githubApiRequest(
        method     : 'POST',
        path       : "/repos/${githubRepoSlug()}/issues/${env.CHANGE_ID}/comments",
        body       : groovy.json.JsonOutput.toJson([body: markdown]),
        description: 'PR comment'
    )
}
