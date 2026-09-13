// Standard OCI labels so a running container can be traced back to the commit
// and build that produced it.
//
// Usage:
//   def labels = imageLabels(cfg)
// Params: cfg (Map) - pipeline config; cfg.appName is read
// Returns: Map of OCI label keys to values
def call(Map cfg) {
    [
        'org.opencontainers.image.source'  : "https://github.com/${githubRepoSlug()}",
        'org.opencontainers.image.revision': env.GIT_COMMIT,
        'org.opencontainers.image.version' : env.APP_VERSION,
        'org.opencontainers.image.title'   : cfg.appName,
        'ci.build.url'                     : env.BUILD_URL ?: '',
    ]
}
