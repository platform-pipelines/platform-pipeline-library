// Moving tags published alongside the immutable version tag.
//
// Usage:
//   def tags = imageExtraTags()
// Returns: List<String> of extra tags (git short sha, plus 'latest' on main/master)
def call() {
    def tags = [env.GIT_SHORT_SHA]
    if (env.BRANCH_NAME in ['main', 'master']) { tags << 'latest' }
    return tags
}
