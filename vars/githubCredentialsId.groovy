// String credential holding a GitHub App installation token or fine-grained
// PAT with contents:write and statuses:write.
//
// Usage:
//   def credId = githubCredentialsId()
// Returns: env.GITHUB_CREDENTIALS_ID if set, else 'github-token'
def call() { env.GITHUB_CREDENTIALS_ID ?: 'github-token' }
