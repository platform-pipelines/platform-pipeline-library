// Slack via incoming webhook — no Slack plugin required.
def call(Map cfg, String status) {
    if (!cfg?.notify?.slackChannel) { return }

    if (!slackShouldNotify(cfg.notify.on, status)) {
        logDebug "Skipping Slack (policy=${cfg.notify.on}, status=${status})"
        return
    }

    writeFile file: '.slack-payload.json',
              text: groovy.json.JsonOutput.toJson(slackPayload(cfg, status))

    withCredentials([string(credentialsId: 'slack-webhook', variable: 'SLACK_WEBHOOK')]) {
        sh(
            script: 'curl -sS -X POST -H "Content-Type: application/json" --data @.slack-payload.json "$SLACK_WEBHOOK" > /dev/null',
            returnStatus: true
        )
    }
    sh 'rm -f .slack-payload.json'
}
