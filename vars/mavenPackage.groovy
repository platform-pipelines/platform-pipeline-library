// Packages a Maven project into its build artifact, stamped with APP_VERSION.
//
// Usage:
//   mavenPackage(cfg)
// Params: cfg (Map) - pipeline config (unused directly; kept for dispatch signature parity)
def call(Map cfg) {
    logBanner 'Package: Java (Maven)'
    sh "mvn ${mavenOpts()} package -DskipTests -Drevision=${env.APP_VERSION}"
}
