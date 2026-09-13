// Signs the image with cosign, keyless, using the build's OIDC identity.
//
// Keyless means no private key to store or rotate: the signature is bound to
// the workload identity that produced it and recorded in a transparency log.
//
// Worth being blunt about: signing only matters if something verifies. Pair
// this with an admission policy (Kyverno, Sigstore policy-controller) that
// rejects unsigned images, or it is paperwork.
//
// Usage:
//   signImage(cfg)
// Params: cfg (Map) - pipeline config; reads cfg.quality.signImage/sbom and cfg.imageRepo
// Returns: nothing; a no-op unless cfg.quality.signImage is true and env.IMAGE_DIGEST is set
def call(Map cfg) {
    if (!cfg.quality.signImage) {
        logDebug 'Image signing disabled'
        return
    }
    if (!env.IMAGE_DIGEST) {
        error 'Cannot sign: no image digest. buildImage must run first.'
    }

    logBanner 'Sign image'

    // Sign the digest, never the tag. A tag can be repointed at different
    // content after signing; a digest cannot.
    def ref = "${cfg.imageRepo}@${env.IMAGE_DIGEST}"

    // COSIGN_EXPERIMENTAL is not set: keyless has been the default since
    // cosign 2.0 and the variable no longer does anything.
    withCredentials([string(credentialsId: 'cosign-oidc-token', variable: 'COSIGN_TOKEN')]) {
        sh "cosign sign --yes --identity-token \$COSIGN_TOKEN ${ref}"

        if (cfg.quality.sbom && fileExists('sbom.cdx.json')) {
            sh """
                cosign attest --yes \\
                  --identity-token \$COSIGN_TOKEN \\
                  --predicate sbom.cdx.json \\
                  --type cyclonedx \\
                  ${ref}
            """
            logInfo 'SBOM attached as a signed attestation'
        }
    }

    logAudit('image.signed', [image: ref])
}
