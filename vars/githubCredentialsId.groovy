// String credential holding a GitHub App installation token or fine-grained
// PAT with contents:write and statuses:write.
def call() { env.GITHUB_CREDENTIALS_ID ?: 'github-token' }
