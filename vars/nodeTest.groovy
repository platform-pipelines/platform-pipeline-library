def call(Map cfg) {
    logBanner 'Test: Node'
    sh 'npm test'
}
