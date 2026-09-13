// Application deploy: bump the image tag in the GitOps repo and let Argo CD
// converge. Jenkins never touches the cluster.
//
// Usage:
//   deployGitops(cfg, envCfg)
// Params: cfg (Map) - pipeline config; reads cfg.imageRepo
//         envCfg (Map) - target environment config; envCfg.name/namespace
// Returns: nothing; commits the manifest bump and waits for Argo CD to sync
def call(Map cfg, Map envCfg) {
    def image = "${cfg.imageRepo}:${env.IMAGE_TAG}"

    updateManifest(cfg: cfg, env: envCfg, image: image)

    logAudit('deploy', [environment: envCfg.name, image: image, namespace: envCfg.namespace])

    argoSync(cfg, envCfg)
}
