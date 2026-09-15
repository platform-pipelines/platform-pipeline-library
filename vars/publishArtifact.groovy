// Publishes build output to every artifact store the repo turned on: Nexus
// (publish.nexusRepo) and GitHub Packages in the repo's own namespace
// (publish.githubPackages). Both can be on at once.
//
// Usage:
//   publishArtifact(cfg)
// Params: cfg (Map) - pipeline config; reads cfg.publish.nexusRepo, cfg.publish.githubPackages, cfg.buildTool
// No-op when no store is configured, the toolchain has nothing to publish, or
// no files match the artifact glob.
def call(Map cfg) {
    def nexus  = cfg.publish?.nexusRepo
    def github = cfg.publish?.githubPackages
    def glob   = appArtifacts(cfg)

    if (!nexus && !github) { logDebug 'No publish.nexusRepo or publish.githubPackages configured — skipping publish'; return }
    if (!glob) { logDebug 'This toolchain produces no publishable artifact'; return }

    def files = findFiles(glob: glob)
    if (!files) { logWarn "No files matched ${glob}"; return }

    if (nexus)  { publishArtifactNexus(cfg, files as List) }
    if (github) { publishArtifactGithub(cfg, files as List) }
}
