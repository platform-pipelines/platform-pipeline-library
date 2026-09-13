// Usage:
//   gradlePackage(cfg)
// Params: cfg (Map) - pipeline config (unused; kept for dispatcher parity)
// Returns: nothing; runs `gradle assemble` stamped with env.APP_VERSION
def call(Map cfg) {
    logBanner 'Package: Java (Gradle)'
    sh "gradle ${gradleOpts()} assemble -Pversion=${env.APP_VERSION}"
}
