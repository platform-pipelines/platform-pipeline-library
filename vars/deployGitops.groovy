// Application deploy: bump the image tag in the GitOps repo and let Argo CD
// converge. Jenkins never touches the cluster.
def call(Map cfg, Map envCfg) {
    def image = "${cfg.imageRepo}:${env.IMAGE_TAG}"

    updateManifest(cfg: cfg, env: envCfg, image: image)

    logAudit('deploy', [environment: envCfg.name, image: image, namespace: envCfg.namespace])

    argoSync(cfg, envCfg)
}
