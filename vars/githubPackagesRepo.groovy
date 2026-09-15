// GitHub Container Registry path that belongs to the repo being built, so
// images and build output live in GitHub Packages next to the code instead of
// in a registry path configured (and eventually drifting) in config.yaml.
//
// GHCR rejects upper-case names, so the whole path is lowercased. GHCR links a
// package to its repository through the org.opencontainers.image.source
// label/annotation, which imageLabels and publishArtifactGithub both set.
//
// Usage:
//   def repo = githubPackagesRepo()                         // 'ghcr.io/acme/orders-api'
//   def repo = githubPackagesRepo('orders-api-artifacts')   // 'ghcr.io/acme/orders-api/orders-api-artifacts'
// Params: name (String) - optional package name nested under the repo path
// Returns: the registry path without a tag; errors if name is not a valid package name
def call(String name = null) {
    def base = "ghcr.io/${githubRepoSlug()}".toString().toLowerCase()
    if (!name) { return base }

    def sub = name.toLowerCase()
    if (!(sub ==~ /^[a-z0-9]+(?:[._-][a-z0-9]+)*(?:\/[a-z0-9]+(?:[._-][a-z0-9]+)*)*$/)) {
        error "'${name}' is not a valid package name under ${base}"
    }
    return "${base}/${sub}".toString()
}
