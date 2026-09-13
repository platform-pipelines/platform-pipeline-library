// Overridable for GitHub Enterprise.
def call() { env.GITHUB_API_URL ?: 'https://api.github.com' }
