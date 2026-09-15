// Uploads build output to Nexus over REST, so the same command works from a
// laptop as from an agent.
//
// Usage:
//   publishArtifactNexus(cfg, findFiles(glob: appArtifacts(cfg)) as List)
// Params: cfg (Map) - pipeline config; reads cfg.publish.nexusRepo and cfg.appName
//         files (List) - files to upload; each needs .path and .name
// Returns: nothing; fails the build if an upload fails
def call(Map cfg, List files) {
    def repo = cfg.publish.nexusRepo

    logBanner "Publish to Nexus (${repo})"

    withCredentials([usernamePassword(
        credentialsId   : 'nexus-credentials',
        usernameVariable: 'NEXUS_USER',
        passwordVariable: 'NEXUS_PASS'
    )]) {
        files.each { file ->
            def url = "${env.NEXUS_URL}/repository/${repo}/${cfg.appName}/${env.APP_VERSION}/${file.name}".toString()
            sh """
                curl -sS --fail \\
                  -u "\$NEXUS_USER:\$NEXUS_PASS" \\
                  --upload-file ${shellQuote(file.path.toString())} \\
                  ${shellQuote(url)}
            """
            logInfo "Published ${file.name}"
        }
    }

    logAudit('artifact.publish', [store: 'nexus', repository: repo, version: env.APP_VERSION, count: files.size()])
}
