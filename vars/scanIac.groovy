// Security scan over infrastructure code.
//
// Trivy's config scanner covers Terraform and CloudFormation (it absorbed
// tfsec), so this reuses a tool already in the toolbox rather than adding one.
def call(Map cfg) {
    logBanner 'Scan: infrastructure code'

    def dir = cfg.infra.workingDir
    def severities = cfg.quality.trivyFailOn.join(',')

    inToolContainer('aquasec/trivy:latest') {
        sh "trivy config --format json  --output trivy-iac.json ${dir} --no-progress || true"
        sh "trivy config --format table --output trivy-iac.txt  ${dir} --no-progress || true"

        archiveArtifacts artifacts: 'trivy-iac.*', allowEmptyArchive: true

        def status = sh(
            script: "trivy config --severity ${severities} --exit-code 1 --no-progress --quiet ${dir}",
            returnStatus: true
        )

        logAudit('security.iac', [tool: 'trivy-config', dir: dir])

        if (status != 0) {
            githubSetStatus('ci/iac-scan', 'failure', "Misconfigurations at ${severities}")
            error "Trivy found ${severities} misconfigurations in ${dir}. See trivy-iac.txt"
        }
        githubSetStatus('ci/iac-scan', 'success', 'No blocking misconfigurations')
    }
}
