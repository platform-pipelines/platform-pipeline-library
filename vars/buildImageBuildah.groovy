// Buildah, for agents running the toolbox image with buildah installed.
//
// Rootless buildah still needs subuid/subgid ranges and fuse-overlayfs on the
// host, so it is not a drop-in for every fleet — hence opt-in.
//
// Usage:
//   buildImageBuildah(cfg, dockerConfig)
// Params: cfg (Map) - pipeline config; reads cfg.dockerfile and cfg.imageRepo
//         dockerConfig (String) - directory holding the registry config.json (from kanikoDockerConfig)
// Returns: nothing; builds and pushes the image plus every extra tag
def call(Map cfg, String dockerConfig) {
    def tags = ([env.IMAGE_TAG] + imageExtraTags())
    def labels = imageLabels(cfg).findAll { k, v -> v }
                                 .collect { k, v -> "--label ${k}='${v}'" }.join(' ')
    def authFile = shellQuote("${dockerConfig}/config.json")

    sh """
        buildah bud \\
          --authfile ${authFile} \\
          --file ${cfg.dockerfile} \\
          --build-arg APP_VERSION=${env.APP_VERSION} \\
          ${labels} \\
          --tag ${cfg.imageRepo}:${env.IMAGE_TAG} \\
          .
    """

    tags.each { tag ->
        sh "buildah push --authfile ${authFile} --digestfile image-digest.txt ${cfg.imageRepo}:${env.IMAGE_TAG} docker://${cfg.imageRepo}:${tag}"
    }
}
