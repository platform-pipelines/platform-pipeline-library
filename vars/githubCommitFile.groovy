// Commits one file to another repo via the contents API, so the GitOps bump
// needs no clone and no push credentials.
def call(Map args) {
    def slug    = args.repo
    def branch  = args.branch ?: 'main'
    def path    = args.path
    def message = args.message ?: "chore: update ${path}"

    writeFile file: '.gh-upload', text: args.content
    def encoded = sh(script: 'base64 -w0 .gh-upload', returnStdout: true).trim()
    sh 'rm -f .gh-upload'

    def payload = [message: message, content: encoded, branch: branch]

    // The contents API needs the blob sha to replace an existing file, and
    // rejects it when creating a new one.
    def existing = githubFileSha(slug, branch, path)
    if (existing) { payload.sha = existing }

    def ok = githubApiRequest(
        method     : 'PUT',
        path       : "/repos/${slug}/contents/${path}",
        body       : groovy.json.JsonOutput.toJson(payload),
        description: "commit ${path}"
    )

    // Unlike a status update, a failed GitOps commit means the deploy did not
    // happen. That must fail the build rather than pass quietly.
    if (!ok) { error "Failed to commit ${path} to ${slug}@${branch}" }
}
