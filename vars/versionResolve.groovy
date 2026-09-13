// Version derived from git, not from a Jenkins counter alone, so a version
// string still traces to a commit after Jenkins is rebuilt from scratch.
//
//   main       -> 1.4.0
//   release/*  -> 1.4.0-rc.42
//   hotfix/*   -> 1.4.0-hotfix.42.gab12cd3
//   feature/*  -> 1.4.0-feature-login.42.gab12cd3
def call(String branch = null) {
    def b     = branch ?: env.BRANCH_NAME
    def base  = versionBase()
    def build = env.BUILD_NUMBER ?: '0'
    def sha   = versionShortSha()

    if (b in ['main', 'master'])  { return base }
    if (b?.startsWith('release/')) { return "${base}-rc.${build}" }
    if (b?.startsWith('hotfix/'))  { return "${base}-hotfix.${build}.g${sha}" }

    return "${base}-${versionSlug(b ?: 'detached')}.${build}.g${sha}"
}
