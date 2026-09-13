def call(Map cfg) {
    logBanner 'Package: Java (Maven)'
    sh "mvn ${mavenOpts()} package -DskipTests -Drevision=${env.APP_VERSION}"
}
