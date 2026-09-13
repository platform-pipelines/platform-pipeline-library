// Polls SonarQube for the quality gate result.
//
// Uses the API rather than the plugin's waitForQualityGate(), which needs a
// webhook back into Jenkins — awkward when Jenkins is not publicly reachable.
//
// Usage:
//   sonarWaitForGate(cfg)
// Params: cfg (Map) - pipeline config; reads cfg.quality.sonarProjectKey for the error/dashboard link
// Returns: nothing; polls up to 10 minutes and errors out unless the gate result is OK
def call(Map cfg) {
    def taskFile = '.scannerwork/report-task.txt'
    if (!fileExists(taskFile)) {
        logWarn 'No report-task.txt — cannot check the quality gate'
        return
    }

    def taskUrl = readProperties(file: taskFile).ceTaskUrl
    def script  = useScript('sonar_gate.py')
    def gate    = null

    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
        timeout(time: 10, unit: 'MINUTES') {
            waitUntil(initialRecurrencePeriod: 5000) {
                gate = sh(
                    script: "python3 ${script} '${taskUrl}' '${env.SONAR_HOST_URL}' \"\$SONAR_TOKEN\"",
                    returnStdout: true
                ).trim()
                logDebug "Gate: ${gate}"
                return gate != 'PENDING'
            }
        }
    }

    logInfo "Quality gate: ${gate}"
    logAudit('quality.gate', [result: gate, project: cfg.quality.sonarProjectKey])

    if (gate != 'OK') {
        githubSetStatus('ci/sonar', 'failure', "Quality gate ${gate}")
        error "SonarQube quality gate failed (${gate}) — ${env.SONAR_HOST_URL}/dashboard?id=${cfg.quality.sonarProjectKey}"
    }
    githubSetStatus('ci/sonar', 'success', 'Quality gate passed')
}
