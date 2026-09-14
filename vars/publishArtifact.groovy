// Uploads build output to Nexus over REST, so the same command works from a
// laptop as from an agent.
//
// Usage:
//   publishArtifact(cfg)
// Params: cfg (Map) - pipeline config; reads cfg.publish.nexusRepo and cfg.appName
// No-op when publish.nexusRepo is not configured, the toolchain has nothing to
// publish, or no files match the artifact glob.
def call(Map cfg) {
    def repo = cfg.publish?.nexusRepo
    def glob = appArtifacts(cfg)

    if (!repo) { logDebug 'No publish.nexusRepo configured — skipping publish'; return }
    if (!glob) { logDebug 'This toolchain produces no publishable artifact'; return }

    def files = findFiles(glob: glob)
    if (!files) { logWarn "No files matched ${glob}"; return }

    logBanner "Publish to Nexus (${repo})"

    withCredentials([usernamePassword(
        credentialsId   : 'nexus-credentials',
        usernameVariable: 'NEXUS_USER',
        passwordVariable: 'NEXUS_PASS'
    )]) {
        files.each { file ->
            sh """
                curl -sS --fail \\
                  -u "\$NEXUS_USER:\$NEXUS_PASS" \\
                  --upload-file "${file.path}" \\
                  "${env.NEXUS_URL}/repository/${repo}/${cfg.appName}/${env.APP_VERSION}/${file.name}"
            """
            logInfo "Published ${file.name}"
        }
    }

    logAudit('artifact.publish', [repository: repo, version: env.APP_VERSION, count: files.size()])
}
