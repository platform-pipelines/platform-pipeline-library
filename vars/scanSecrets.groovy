// Gitleaks across the repo history, not just HEAD. A secret committed and
// later reverted is still a leaked secret.
def call(Map cfg) {
    logBanner 'Secret scan'

    def config = fileExists('.gitleaks.toml') ? '--config .gitleaks.toml' : ''

    def status = inToolContainer('zricethezav/gitleaks:latest') {
        return sh(
            script: "gitleaks detect --source . ${config} --report-format json --report-path gitleaks-report.json --redact --exit-code 1 --no-banner",
            returnStatus: true
        )
    }

    archiveArtifacts artifacts: 'gitleaks-report.json', allowEmptyArchive: true

    if (status != 0) {
        logAudit('security.secrets', [result: 'FOUND'])
        githubSetStatus('ci/secrets', 'failure', 'Potential secrets detected')
        error 'Gitleaks found potential secrets. Rotate anything real, then allowlist false positives in .gitleaks.toml'
    }

    logAudit('security.secrets', [result: 'CLEAN'])
    githubSetStatus('ci/secrets', 'success', 'No secrets detected')
}
