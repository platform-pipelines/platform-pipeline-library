// Checkout, load config, set the version environment variables.
//
// Usage:
//   def cfg = initPipeline(overrides)
// Params: overrides (Map) - inline config overrides passed through to configLoad
// Returns: the loaded, validated pipeline config Map
def call(Map overrides) {
    logBanner 'Initialise'

    checkout scm

    def cfg = configLoad(overrides)

    env.APP_NAME      = cfg.appName
    env.APP_VERSION   = versionResolve(env.BRANCH_NAME)
    env.IMAGE_TAG     = versionImageTag(env.BRANCH_NAME)
    env.GIT_SHORT_SHA = versionShortSha()

    currentBuild.displayName = "#${env.BUILD_NUMBER} ${env.APP_VERSION}"
    currentBuild.description = "${cfg.buildTool} · ${env.GIT_SHORT_SHA}"

    logInfo "app=${cfg.appName} version=${env.APP_VERSION} tool=${cfg.buildTool}"
    logAudit('pipeline.start', [version: env.APP_VERSION])

    if (cfg.notify.githubChecks) {
        githubSetStatus('ci/jenkins', 'pending', 'Pipeline running')
    }

    return cfg
}
