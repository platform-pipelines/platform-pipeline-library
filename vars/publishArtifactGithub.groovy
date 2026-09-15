// Pushes build output to GitHub Packages, in the same repository as the code,
// as one OCI artifact in the GitHub Container Registry:
//
//   ghcr.io/<owner>/<repo>/<appName>-artifacts:<IMAGE_TAG>
//
// GitHub Packages has native Maven and npm registries but nothing for Go
// binaries, wheels, plans or arbitrary files. ORAS pushes every toolchain's
// output the same way, and `oras pull <ref>` gets it back. The
// org.opencontainers.image.source annotation links the package to the repo, so
// it appears on the repo's Packages tab beside the container image.
//
// Credentials are the ghcr.io entry withRegistryAuth writes, whatever registry
// imageRepo points at, so an ECR image can still publish artifacts to GitHub.
//
// Usage:
//   publishArtifactGithub(cfg, findFiles(glob: appArtifacts(cfg)) as List)
// Params: cfg (Map) - pipeline config; reads cfg.appName
//         files (List) - files to push; each needs .path (relative to the workspace) and .name
// Returns: the pushed reference, e.g. 'ghcr.io/acme/orders-api/orders-api-artifacts:1.4.0'
def call(Map cfg, List files) {
    if (!env.IMAGE_TAG) {
        error 'Cannot publish to GitHub Packages: IMAGE_TAG is not set (initPipeline sets it)'
    }

    def repo = githubPackagesRepo("${cfg.appName}-artifacts".toString())
    def ref  = "${repo}:${env.IMAGE_TAG}".toString()

    logBanner "Publish to GitHub Packages (${ref})"

    def annotations = imageLabels(cfg)
        .findAll { k, v -> v }
        .collect { k, v -> "--annotation ${shellQuote("${k}=${v}".toString())}" }
        .join(' ')
    def paths = files.collect { shellQuote(it.path.toString()) }.join(' ')

    withRegistryAuth(cfg + [imageRepo: repo]) {
        inToolContainer('ghcr.io/oras-project/oras:v1.3.4') {
            sh 'oras push --registry-config "$DOCKER_CONFIG/config.json" ' +
               "--artifact-type application/vnd.platformpipelines.build-output ${annotations} ${shellQuote(ref)} ${paths}"
        }
    }

    files.each { logInfo "Published ${it.name}" }
    logAudit('artifact.publish', [store: 'github', repository: ref, version: env.APP_VERSION, count: files.size()])
    return ref
}
