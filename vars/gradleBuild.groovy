// Usage:
//   gradleBuild(cfg)
// Params: cfg (Map) - pipeline config (unused; kept for dispatcher parity)
// Returns: nothing; runs `gradle classes`
def call(Map cfg) {
    logBanner 'Build: Java (Gradle)'
    sh "gradle ${gradleOpts()} classes"
}
