def call(Map cfg) {
    logBanner 'Build: Node'
    nodeInstall(cfg)
    sh 'npm run build --if-present'
}
