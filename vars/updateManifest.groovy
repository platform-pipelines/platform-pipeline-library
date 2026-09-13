// The CI to CD handoff. Jenkins never talks to the cluster — it commits a new
// image tag to the GitOps repo and Argo CD converges.
//
// Rollback is a git revert, and cluster state is auditable from git history
// without asking Jenkins anything.
def call(Map args) {
    def cfg    = args.cfg
    def envCfg = args.env
    def image  = args.image

    logBanner "GitOps bump: ${envCfg.name}"

    def current = githubFetchFile(
        repo  : cfg.gitopsRepo,
        branch: cfg.gitopsBranch,
        path  : envCfg.manifestPath
    )

    def updated = manifestBumpImage(current, image)

    if (updated == current) {
        logInfo "Manifest already at ${image} — nothing to commit"
        return
    }

    def message = """deploy(${envCfg.name}): ${cfg.appName} ${env.APP_VERSION}

Image: ${image}
Commit: ${env.GIT_COMMIT}
Build: ${env.BUILD_URL}
Approved-by: ${env.DEPLOY_APPROVER ?: logActor()}"""

    githubCommitFile(
        repo   : cfg.gitopsRepo,
        branch : cfg.gitopsBranch,
        path   : envCfg.manifestPath,
        content: updated,
        message: message
    )

    logInfo "Committed ${envCfg.manifestPath} -> ${image}"
}
