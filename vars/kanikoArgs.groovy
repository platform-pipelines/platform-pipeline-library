// The kaniko executor flags, shared by both kaniko implementations.
//
// Usage:
//   sh "/kaniko/executor ${kanikoArgs(cfg)}"
// Params: cfg (Map) - pipeline config; reads cfg.imageRepo and cfg.dockerfile
// Returns: a string of kaniko executor CLI flags
def call(Map cfg) {
    def destinations = ([env.IMAGE_TAG] + imageExtraTags())
        .collect { "--destination=${cfg.imageRepo}:${it}" }.join(' ')

    def labels = imageLabels(cfg)
        .findAll { k, v -> v }
        .collect { k, v -> "--label ${k}='${v}'" }.join(' ')

    return """--dockerfile=${cfg.dockerfile} \
${destinations} \
${labels} \
--build-arg APP_VERSION=${env.APP_VERSION} \
--cache=true \
--cache-repo=${cfg.imageRepo}/cache \
--snapshot-mode=redo \
--digest-file=image-digest.txt"""
}
