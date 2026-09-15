// Finds the GitHub release for a tag, creating it when it does not exist yet,
// so a rebuild of the same version adds to its release instead of failing.
//
// A new tag is created at the commit being built. When the tag already exists
// in git, GitHub attaches the release to that tag as-is.
//
// Usage:
//   def release = githubRelease(repo: 'acme/api', tag: 'v1.4.0')
// Params: args.repo (String) - owner/name of the repo; must match owner/name shape
//         args.tag (String) - tag the release belongs to
//         args.commit (String) - commit a newly created tag points at (default env.GIT_COMMIT)
//         args.prerelease (boolean) - mark a newly created release as a pre-release (default false)
// Returns: the release as a Map (id, html_url, upload_url, assets, ...); errors if it can be neither read nor created
def call(Map args) {
    def slug = args.repo
    def tag  = args.tag

    if (!(slug =~ /^[\w.-]+\/[\w.-]+$/)) { error "githubRelease: invalid repo slug '${slug}'" }
    if (!(tag =~ /^[\w.+-]+$/))          { error "githubRelease: invalid tag '${tag}'" }

    def base    = "${githubApiUrl()}/repos/${slug}/releases"
    def payload = groovy.json.JsonOutput.toJson([
        tag_name        : tag,
        target_commitish: args.commit ?: env.GIT_COMMIT,
        name            : tag,
        prerelease      : args.prerelease as boolean,
    ])
    def out = '.gh-release.json'

    def code = withCredentials([string(credentialsId: githubCredentialsId(), variable: 'GH_TOKEN')]) {
        sh(
            script: """
                code=\$(curl -sS -o ${out} -w '%{http_code}' \\
                    -H "Authorization: Bearer \$GH_TOKEN" \\
                    -H "Accept: application/vnd.github+json" \\
                    ${shellQuote("${base}/tags/${tag}")})
                if [ "\$code" = 404 ]; then
                    code=\$(curl -sS -o ${out} -w '%{http_code}' -X POST \\
                        -H "Authorization: Bearer \$GH_TOKEN" \\
                        -H "Accept: application/vnd.github+json" \\
                        -H "Content-Type: application/json" \\
                        -d ${shellQuote(payload)} \\
                        ${shellQuote(base)})
                fi
                echo "\$code"
            """,
            returnStdout: true
        ).trim()
    }

    def body = readFile(out)
    sh "rm -f ${out}"

    if (!(code ==~ /2\d\d/)) {
        error "Could not read or create release ${tag} in ${slug} (HTTP ${code}): ${body}"
    }
    return readJSON(text: body)
}
