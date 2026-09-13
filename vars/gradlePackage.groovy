def call(Map cfg) {
    logBanner 'Package: Java (Gradle)'
    sh "gradle ${gradleOpts()} assemble -Pversion=${env.APP_VERSION}"
}
