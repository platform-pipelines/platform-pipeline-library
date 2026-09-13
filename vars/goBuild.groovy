// Static binary, version stamped into main.version.
def call(Map cfg) {
    logBanner 'Build: Go'
    sh 'go mod download'
    sh 'go build ./...'
}
