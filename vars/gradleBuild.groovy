def call(Map cfg) {
    logBanner 'Build: Java (Gradle)'
    sh "gradle ${gradleOpts()} classes"
}
