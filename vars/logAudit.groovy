// Append one JSON line to .ci-audit.jsonl, archived at the end of the run.
//
// Console logs rotate; this file is the durable answer to "who deployed what,
// when, from which commit" when someone asks six months later.
def call(String action, Map details = [:]) {
    def record = [
        timestamp: new Date().format("yyyy-MM-dd'T'HH:mm:ss'Z'", TimeZone.getTimeZone('UTC')),
        action   : action,
        job      : env.JOB_NAME,
        build    : env.BUILD_NUMBER,
        commit   : env.GIT_COMMIT,
        branch   : env.BRANCH_NAME,
        actor    : logActor(),
    ] + details

    def line = groovy.json.JsonOutput.toJson(record)
    writeFile file: '.audit-line', text: line + '\n'
    sh 'cat .audit-line >> .ci-audit.jsonl && rm -f .audit-line'
    echo "[AUDIT] ${action} ${details}"
}
