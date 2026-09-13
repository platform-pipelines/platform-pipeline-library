// One curl-based REST call to GitHub.
//
// Returns true on success. A non-2xx logs and returns false rather than
// failing the build — a flaky status update should not sink a green build.
// Callers that must not fail silently (githubCommitFile) check the result.
def call(Map args) {
    def method = args.method ?: 'POST'
    def path   = args.path
    def body   = args.body ?: '{}'
    def what   = args.description ?: "${method} ${path}"

    def ok = withCredentials([string(credentialsId: githubCredentialsId(), variable: 'GH_TOKEN')]) {
        def status = sh(
            script: """
                code=\$(curl -sS -o /tmp/gh-resp.json -w '%{http_code}' \\
                    -X ${method} \\
                    -H "Authorization: Bearer \$GH_TOKEN" \\
                    -H "Accept: application/vnd.github+json" \\
                    -H "Content-Type: application/json" \\
                    -d ${shellQuote(body)} \\
                    "${githubApiUrl()}${path}")
                if [ "\$code" -ge 300 ]; then
                    echo "GitHub ${method} ${path} -> \$code" >&2
                    cat /tmp/gh-resp.json >&2
                    exit 1
                fi
            """,
            returnStatus: true
        )
        return status == 0
    }

    if (ok) { logDebug "GitHub ok: ${what}" } else { logWarn "GitHub call failed: ${what}" }
    return ok
}
