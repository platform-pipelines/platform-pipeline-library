// Compiles a Maven project.
//
// Usage:
//   mavenBuild(cfg)
// Params: cfg (Map) - pipeline config (unused directly; kept for dispatch signature parity)
def call(Map cfg) {
    logBanner 'Build: Java (Maven)'
    sh "mvn ${mavenOpts()} clean compile"
}
