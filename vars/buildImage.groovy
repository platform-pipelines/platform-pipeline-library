// Builds and pushes the container image.
//
// Routes to an implementation because how you build an image without a root
// daemon depends entirely on where your agents run. Set `imageBuilder:` in
// .ci/config.yaml, or leave it and get the docker-hosted kaniko default.
//
// Usage:
//   buildImage(cfg)
// Params: cfg (Map) - pipeline config; reads cfg.dockerfile, cfg.imageRepo, cfg.imageBuilder
// Returns: nothing; sets env.IMAGE_DIGEST/env.IMAGE_REF and errors if the Dockerfile or imageBuilder is invalid
def call(Map cfg) {
    if (!fileExists(cfg.dockerfile)) {
        error "Dockerfile not found at ${cfg.dockerfile}"
    }

    logBanner "Build image ${cfg.imageRepo}:${env.IMAGE_TAG}"

    kanikoDockerConfig(cfg)

    switch (cfg.imageBuilder) {
        case 'kaniko-k8s':    buildImageKanikoK8s(cfg);    break
        case 'kaniko-docker': buildImageKanikoDocker(cfg); break
        case 'buildah':       buildImageBuildah(cfg);      break
        default:
            error "Unknown imageBuilder '${cfg.imageBuilder}'. Use: ${configImageBuilders().join(', ')}"
    }

    env.IMAGE_DIGEST = fileExists('image-digest.txt') ? readFile('image-digest.txt').trim() : ''
    env.IMAGE_REF    = "${cfg.imageRepo}:${env.IMAGE_TAG}"

    logInfo "Pushed ${env.IMAGE_REF} (${env.IMAGE_DIGEST})"
    logAudit('image.push', [image: env.IMAGE_REF, digest: env.IMAGE_DIGEST, builder: cfg.imageBuilder])
}
