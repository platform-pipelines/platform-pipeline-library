// Trivy scan of a filesystem or an image.
//
//   scanTrivy(cfg: cfg, target: '.', type: 'fs')
//   scanTrivy(cfg: cfg, target: 'ghcr.io/acme/api:1.4.0', type: 'image')
def call(Map args) {
    def cfg    = args.cfg
    def target = args.target ?: '.'
    def type   = args.type ?: 'fs'
    def report = "trivy-${type}"

    def severities = cfg.quality.trivyFailOn.join(',')
    def unfixed    = cfg.quality.trivyIgnoreUnfixed ? '--ignore-unfixed' : ''
    def ignore     = fileExists('.trivyignore') ? '--ignorefile .trivyignore' : ''

    logBanner "Trivy ${type}: ${target}"

    inToolContainer('aquasec/trivy:latest') {

        // Reports first, always. A failing scan should still leave something
        // readable behind rather than only an exit code.
        sh "trivy ${type} --format json  --output ${report}.json ${ignore} --no-progress ${target} || true"
        sh "trivy ${type} --format table --output ${report}.txt  ${ignore} --no-progress ${target} || true"

        archiveArtifacts artifacts: "${report}.*", allowEmptyArchive: true

        def summary = trivySummary("${report}.json")
        logAudit('security.scan', [tool: 'trivy', type: type, target: target, findings: summary])
        logInfo "Findings: ${summary}"

        def status = sh(
            script: "trivy ${type} --severity ${severities} ${unfixed} ${ignore} --exit-code 1 --no-progress --quiet ${target}",
            returnStatus: true
        )

        if (status != 0) {
            githubSetStatus("ci/trivy-${type}", 'failure', "Vulnerabilities at ${severities}")
            error "Trivy found ${severities} vulnerabilities in ${target}. See ${report}.txt"
        }
        githubSetStatus("ci/trivy-${type}", 'success', 'No blocking vulnerabilities')
    }
}
