// Checkstyle plus SpotBugs. Both write XML that Jenkins can surface, and both
// run offline once the plugin jars are cached.
def call(Map cfg) {
    logBanner 'Lint: Java (Maven)'

    if (cfg.lint.autoFormat) {
        sh "mvn ${mavenOpts()} spotless:apply || true"
    }

    def status = sh(
        script: "mvn ${mavenOpts()} checkstyle:check spotbugs:check",
        returnStatus: true
    )

    if (status != 0 && cfg.lint.failOnError) {
        error 'Checkstyle or SpotBugs reported violations'
    }
}
