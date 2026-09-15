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
    files.each { file ->
        githubUploadReleaseAsset(release: release, path: file.path, name: file.name)
        logInfo "Published ${file.name}"
    }

    logAudit('artifact.publish', [release: tag, url: release.html_url, count: files.size()])
}
