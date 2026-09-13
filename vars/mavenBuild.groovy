def call(Map cfg) {
    logBanner 'Build: Java (Maven)'
    sh "mvn ${mavenOpts()} clean compile"
}
