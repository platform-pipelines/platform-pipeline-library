// The kaniko executor flags, shared by both kaniko implementations.
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
