def call(Map cfg) {
    logBanner 'Test: Java (Gradle)'
    sh "gradle ${gradleOpts()} test jacocoTestReport"
}
