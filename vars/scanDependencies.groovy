// OWASP Dependency-Check. Opt-in: it is slow and needs a warm NVD cache to
// be tolerable, so most repos rely on Trivy instead.
//
// Usage:
//   scanDependencies(cfg)
// Params: cfg (Map) - pipeline config; reads cfg.appName and cfg.quality.dependencyCheckCvss (default 7)
// Returns: nothing; archives the report and errors out if a CVSS-scored CVE meets the threshold
def call(Map cfg) {
    logBanner 'Dependency-Check'

    def threshold = cfg.quality.dependencyCheckCvss ?: 7

    docker.image('owasp/dependency-check:latest').inside("--entrypoint='' -v dc-nvd-cache:/usr/share/dependency-check/data") {
        def status = sh(
            script: """
                /usr/share/dependency-check/bin/dependency-check.sh \\
                  --project "${cfg.appName}" \\
                  --scan . \\
                  --format ALL \\
                  --out dependency-check-report \\
                  --failOnCVSS ${threshold} \\
                  --disableAssembly
            """,
            returnStatus: true
        )

        archiveArtifacts artifacts: 'dependency-check-report/*', allowEmptyArchive: true

        if (status != 0) {
            githubSetStatus('ci/dependency-check', 'failure', "CVSS >= ${threshold}")
            error "Dependency-Check found vulnerabilities at CVSS >= ${threshold}"
        }
        githubSetStatus('ci/dependency-check', 'success', 'No high-severity CVEs')
    }
}
