// Append one JSON line to .ci-audit.jsonl, archived at the end of the run.
//
// Console logs rotate; this file is the durable answer to "who deployed what,
// when, from which commit" when someone asks six months later.
//
// Usage:
//   logAudit('infra.changeset', [environment: envCfg.name, stack: stack])
// Params: action (String) - short dotted event name
//         details (Map) - extra fields merged into the JSON record
def call(String action, Map details = [:]) {
    def record = [
        // java.time, not Date.format(): that is a Groovy extension method that
        // is missing outside Jenkins' bundled Groovy (and from the test runtime).
        timestamp: java.time.Instant.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS).toString(),
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
