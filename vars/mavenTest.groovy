// verify, not test — this runs Surefire and the JaCoCo report goal, which the
// coverage gate and Sonar both depend on.
//
// Usage:
//   mavenTest(cfg)
// Params: cfg (Map) - pipeline config (unused directly; kept for dispatch signature parity)
def call(Map cfg) {
    logBanner 'Test: Java (Maven)'
    sh "mvn ${mavenOpts()} verify -DskipITs"
}
