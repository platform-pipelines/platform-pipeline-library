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

    // What this build will actually do, so nobody has to reconstruct it from
    // YAML plus defaults plus derivation rules.
    def gates = ['sonar', 'trivy', 'secretScan', 'dependencyCheck', 'sbom', 'signImage'].findAll { cfg.quality[it] }
    def targets = configEnvironmentsFor(cfg, env.BRANCH_NAME)*.name
    logInfo "deploy=${cfg.deployStrategy} image=${cfg.containerize ? cfg.imageBuilder : 'none'} " +
            "lint=${cfg.lint.enabled ? (cfg.lint.failOnError ? 'blocking' : 'report-only') : 'off'} " +
            "gates=${gates ? gates.join(',') : 'none'} " +
            "minCoverage=${cfg.quality.minCoverage != null ? cfg.quality.minCoverage : 'off'} " +
            "environments=${targets ? targets.join('->') : 'none for this branch'}"
    logAudit('pipeline.start', [version: env.APP_VERSION])

    if (cfg.notify.githubChecks) {
        githubSetStatus('ci/jenkins', 'pending', 'Pipeline running')
    }

    return cfg
}
