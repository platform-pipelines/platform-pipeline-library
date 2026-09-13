// Rewrites the image reference inside a manifest, leaving everything else —
// comments, ordering, unrelated containers — untouched.
//
// This is the riskiest string handling in the library: a regex that silently
// fails to match means the pipeline reports success while the old image stays
// deployed. Covered by UpdateManifestTest.
def call(String yamlText, String image) {
    // lastIndexOf, not split on ':' — registry.local:5000/acme/api:1.4.0
    // contains two colons and only the last separates the tag.
    def cut = image.lastIndexOf(':')
    if (cut < 0) { error "Image reference '${image}' has no tag" }

    def repo = image.substring(0, cut)
    def tag  = image.substring(cut + 1)
    def out  = yamlText

    // The optional -? in each pattern covers the inline list form
    //   - image: ghcr.io/acme/api:1.2.3
    // which is common in hand-written manifests and silently missed without it.

    // kustomization.yaml:  newTag: 1.2.3
    out = out.replaceAll(/(?m)^(\s*-?\s*newTag:\s*).*$/) { match, prefix -> prefix + tag }

    // plain manifest:  image: ghcr.io/acme/api:1.2.3
    out = out.replaceAll(/(?m)^(\s*-?\s*image:\s*)\Q${repo}\E:\S+$/) { match, prefix -> prefix + repo + ':' + tag }

    // helm values:  tag: "1.2.3"
    // Safe next to newTag: ^\s*tag: cannot match a line reading newTag:.
    out = out.replaceAll(/(?m)^(\s*tag:\s*)["']?[^"'\s]+["']?$/) { match, prefix -> prefix + '"' + tag + '"' }

    return out
}
