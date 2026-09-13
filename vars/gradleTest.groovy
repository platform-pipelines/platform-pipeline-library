// Usage:
//   gradleTest(cfg)
// Params: cfg (Map) - pipeline config (unused; kept for dispatcher parity)
// Returns: nothing; runs `gradle test jacocoTestReport`
def call(Map cfg) {
    logBanner 'Test: Java (Gradle)'
    sh "gradle ${gradleOpts()} test jacocoTestReport"
}
