// Overridable for GitHub Enterprise.
//
// Usage:
//   def url = githubApiUrl()
// Returns: env.GITHUB_API_URL if set, else the public GitHub API base url
def call() { env.GITHUB_API_URL ?: 'https://api.github.com' }
