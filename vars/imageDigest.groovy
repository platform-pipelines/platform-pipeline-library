// Resolves an image tag to its registry digest, failing when the tag does not
// exist.
//
// A CD run that commits a tag nobody pushed goes green and leaves pods in
// ImagePullBackOff; checking the registry first turns that into a failed build
// before anything changes. The digest is recorded so the audit log says which
// bytes were deployed, not just which mutable tag.
//
// Runs crane in its own container on every agent: it is a small static tool
// that the toolbox does not carry.
//
// Usage:
//   withRegistryAuth(cfg) { imageDigest(cfg, env.IMAGE_TAG) }
// Params: cfg (Map) - pipeline config; reads cfg.imageRepo
//         tag (String) - image tag to resolve
// Returns: the digest ('sha256:…'); also sets env.IMAGE_DIGEST
def call(Map cfg, String tag) {
    def ref = "${cfg.imageRepo}:${tag}"

    def digest = inContainer('gcr.io/go-containerregistry/crane:debug', null) {
        return sh(script: "crane digest ${shellQuote(ref)} 2>&1 || true", returnStdout: true).trim()
    }

    if (!(digest ==~ /^sha256:[a-f0-9]{64}$/)) {
        error "Image ${ref} was not found in the registry: ${digest}"
    }

    env.IMAGE_DIGEST = digest
    logInfo "${ref} -> ${digest}"
    return digest
}
