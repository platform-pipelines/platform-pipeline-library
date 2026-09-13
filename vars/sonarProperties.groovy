// Assembles the full -D flag list for sonar-scanner.
def call(Map cfg) {
    def props = [
        'sonar.projectKey'    : cfg.quality.sonarProjectKey,
        'sonar.projectName'   : cfg.appName,
        'sonar.projectVersion': env.APP_VERSION,
        'sonar.sources'       : cfg.extra.sonarSources ?: '.',
        'sonar.exclusions'    : cfg.extra.sonarExclusions ?: '**/node_modules/**,**/target/**,**/build/**,**/dist/**',
        'sonar.scm.revision'  : env.GIT_COMMIT,
    ]

    // PR analysis decorates the pull request; branch analysis updates the
    // long-lived branch. Sending both at once is an error in Sonar.
    if (env.CHANGE_ID) {
        props['sonar.pullrequest.key']    = env.CHANGE_ID
        props['sonar.pullrequest.branch'] = env.CHANGE_BRANCH
        props['sonar.pullrequest.base']   = env.CHANGE_TARGET
    } else {
        props['sonar.branch.name'] = env.BRANCH_NAME
    }

    props += appSonarProps(cfg)

    return props.findAll { k, v -> v }.collect { k, v -> "-D${k}=${v}" }.join(' ')
}
