// Buildah, for agents running the toolbox image with buildah installed.
//
// Rootless buildah still needs subuid/subgid ranges and fuse-overlayfs on the
// host, so it is not a drop-in for every fleet — hence opt-in.
//
// Usage:
//   buildImageBuildah(cfg)
// Params: cfg (Map) - pipeline config; reads cfg.dockerfile and cfg.imageRepo
// Returns: nothing; builds and pushes the image plus every extra tag
def call(Map cfg) {
    def tags = ([env.IMAGE_TAG] + imageExtraTags())
    def labels = imageLabels(cfg).findAll { k, v -> v }
                                 .collect { k, v -> "--label ${k}='${v}'" }.join(' ')

    sh """
        buildah bud \\
          --file ${cfg.dockerfile} \\
          --build-arg APP_VERSION=${env.APP_VERSION} \\
          ${labels} \\
          --tag ${cfg.imageRepo}:${env.IMAGE_TAG} \\
          .
    """

    tags.each { tag ->
        sh "buildah push --digestfile image-digest.txt ${cfg.imageRepo}:${env.IMAGE_TAG} docker://${cfg.imageRepo}:${tag}"
    }
}
