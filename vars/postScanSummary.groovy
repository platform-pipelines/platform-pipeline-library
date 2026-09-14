// Posts a scan summary to the PR, replacing the previous one rather than
// adding another. No-op outside PR builds.
//
// The point is that a developer sees the result without leaving GitHub. A
// scan nobody reads is a scan that does not change behaviour.
//
// Usage:
//   postScanSummary(cfg, [Trivy: 'clean', Secrets: 'none found'])
// Params: cfg (Map) - pipeline config (unused directly; kept for call-site consistency)
//         findings (Map) - check name -> result string, rendered as a table
// No-op outside pull request builds (env.CHANGE_ID unset).
def call(Map cfg, Map findings) {
    if (!env.CHANGE_ID) { return }

    def rows = findings.collect { check, result -> "| ${check} | ${result} |" }.join('\n')

    def body = """### Pipeline scan summary

| Check | Result |
|---|---|
${rows}

Version `${env.APP_VERSION}` · commit `${env.GIT_SHORT_SHA}` · [build log](${env.BUILD_URL})"""

    githubUpsertComment('<!-- platform-pipeline:scan-summary -->', body)
}
