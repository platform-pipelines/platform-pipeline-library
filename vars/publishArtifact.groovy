// Attaches build output to a GitHub release of this repo, tagged v<version>,
// so artifacts live next to the source (and the image in GHCR) with no
// separate artifact store to run or credential.
//
// A version with a pre-release suffix (1.4.0-rc.42) creates a pre-release.
// Rebuilding a version replaces assets of the same name instead of failing.
//
// Usage:
//   publishArtifact(cfg)
// Params: cfg (Map) - pipeline config; reads cfg.publish.githubRelease, cfg.publish.branchPattern and cfg.buildTool
// No-op when publish.githubRelease is off, the branch does not match
// publish.branchPattern, the toolchain has nothing to publish, or no files
// match the artifact glob.
def call(Map cfg) {
    def publish = cfg.publish ?: [:]
    def branch  = env.BRANCH_NAME ?: ''
    def glob    = appArtifacts(cfg)

    if (!publish.githubRelease) { logDebug 'publish.githubRelease is off — skipping publish'; return }
    if (!(branch ==~ configGlobToRegex(publish.branchPattern ?: 'main'))) {
        logDebug "Branch ${branch} does not match publish.branchPattern — skipping publish"
        return
    }
    if (!glob) { logDebug 'This toolchain produces no publishable artifact'; return }

    def files = findFiles(glob: glob)
    if (!files) { logWarn "No files matched ${glob}"; return }

    def slug    = githubRepoSlug()
    def version = env.APP_VERSION
    def tag     = "v${version}"

    logBanner "Publish to GitHub release ${tag} (${slug})"

    def release = githubRelease(repo: slug, tag: tag, prerelease: version.contains('-'))
    // upload_url is a URI template: .../releases/7/assets{?name,label}
    def uploadUrl = release.upload_url.replaceAll(/\{.*\}$/, '')
    def existing  = (release.assets ?: []).collectEntries { [(it.name): it.id] }

    withCredentials([string(credentialsId: githubCredentialsId(), variable: 'GH_TOKEN')]) {
        files.each { file ->
            // Asset names are unique per release, so a rebuild deletes first.
            def assetId = existing[file.name]
            if (assetId) {
                sh """curl -sS --fail -o /dev/null -X DELETE -H "Authorization: Bearer \$GH_TOKEN" ${shellQuote("${githubApiUrl()}/repos/${slug}/releases/assets/${assetId}")}"""
                logInfo "Replacing ${file.name}"
            }
            sh """
                curl -sS --fail -o /dev/null -X POST \\
                  -H "Authorization: Bearer \$GH_TOKEN" \\
                  -H "Content-Type: application/octet-stream" \\
                  --data-binary @${shellQuote(file.path)} \\
                  ${shellQuote("${uploadUrl}?name=${URLEncoder.encode(file.name, 'UTF-8')}")}
            """
            logInfo "Published ${file.name}"
        }
    }

    logAudit('artifact.publish', [release: tag, url: release.html_url, count: files.size()])
}
