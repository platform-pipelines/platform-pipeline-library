// Moving tags published alongside the immutable version tag.
def call() {
    def tags = [env.GIT_SHORT_SHA]
    if (env.BRANCH_NAME in ['main', 'master']) { tags << 'latest' }
    return tags
}
