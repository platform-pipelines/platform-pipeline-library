def call(Map cfg) {
    logBanner 'Package: Node'
    sh "npm version ${env.APP_VERSION} --no-git-tag-version --allow-same-version"
    sh 'npm pack'
}
