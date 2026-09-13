// verify, not test — this runs Surefire and the JaCoCo report goal, which the
// coverage gate and Sonar both depend on.
def call(Map cfg) {
    logBanner 'Test: Java (Maven)'
    sh "mvn ${mavenOpts()} verify -DskipITs"
}
