// Usage:
//   gradleLint(cfg)
// Params: cfg (Map) - pipeline config; cfg.lint.autoFormat/failOnError are read
// Returns: nothing; errors if checkstyle/spotbugs report violations and failOnError is true
def call(Map cfg) {
    logBanner 'Lint: Java (Gradle)'

    if (cfg.lint.autoFormat) {
        sh "gradle ${gradleOpts()} spotlessApply || true"
    }

    def status = sh(
        script: "gradle ${gradleOpts()} checkstyleMain spotbugsMain",
        returnStatus: true
    )

    if (status != 0 && cfg.lint.failOnError) {
        error 'Checkstyle or SpotBugs reported violations'
    }
}
