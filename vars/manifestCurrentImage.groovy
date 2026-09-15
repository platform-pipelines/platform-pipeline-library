import com.cloudbees.groovy.cps.NonCPS

// The image tag a GitOps manifest currently deploys — the read side of
// manifestBumpImage, recognising the same three shapes in the same order.
//
// cdPipeline uses it to promote "whatever staging runs" to prod without anyone
// copying a tag by hand.
//
// Usage:
//   def tag = manifestCurrentImage(yamlText, 'ghcr.io/acme/api')   // '1.4.0'
// Params: yamlText (String) - manifest content (kustomization, plain manifest or Helm values)
//         imageRepo (String) - image repository without a tag
// Returns: the tag String, or null when the manifest references no tag for that repo
@NonCPS
def call(String yamlText, String imageRepo) {
    if (!yamlText) { return null }

    // plain manifest:  image: ghcr.io/acme/api:1.2.3
    def plain = yamlText =~ /(?m)^\s*-?\s*image:\s*["']?\Q${imageRepo}\E:([^\s"'@]+)/
    if (plain.find()) { return plain.group(1) }

    // kustomization.yaml:  newTag: 1.2.3
    def kustomize = yamlText =~ /(?m)^\s*-?\s*newTag:\s*["']?([^\s"']+)/
    if (kustomize.find()) { return kustomize.group(1) }

    // helm values:  tag: "1.2.3"
    def helm = yamlText =~ /(?m)^\s*tag:\s*["']?([^\s"']+)/
    if (helm.find()) { return helm.group(1) }

    return null
}
