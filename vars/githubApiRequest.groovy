// One curl-based REST call to GitHub.
//
// Returns true on success. A non-2xx logs and returns false rather than
// failing the build — a flaky status update should not sink a green build.
// Callers that must not fail silently (githubCommitFile) check the result.
//
// Usage:
//   githubApiRequest(method: 'POST', path: '/repos/acme/api/issues/1/comments', body: json)
// Params: args.method (String) - HTTP verb; must be GET/POST/PUT/PATCH/DELETE
//         args.path (String) - API path appended to githubApiUrl()
//         args.body (String) - JSON request body (default '{}')
//         args.description (String) - label used in log messages
// Returns: true on a non-3xx+ response, false otherwise
def call(Map args) {
    def method = args.method ?: 'POST'
    def path   = args.path
    def body   = args.body ?: '{}'
    def what   = args.description ?: "${method} ${path}"

    def allowedMethods = ['GET', 'POST', 'PUT', 'PATCH', 'DELETE']
    if (!(method in allowedMethods)) {
        error "githubApiRequest: unsupported method '${method}' (use: ${allowedMethods.join(', ')})"
    }

    def ok = withCredentials([string(credentialsId: githubCredentialsId(), variable: 'GH_TOKEN')]) {
        def status = sh(
            script: """
                code=\$(curl -sS -o /tmp/gh-resp.json -w '%{http_code}' \\
                    -X ${method} \\
                    -H "Authorization: Bearer \$GH_TOKEN" \\
                    -H "Accept: application/vnd.github+json" \\
                    -H "Content-Type: application/json" \\
                    -d ${shellQuote(body)} \\
                    ${shellQuote("${githubApiUrl()}${path}")})
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
