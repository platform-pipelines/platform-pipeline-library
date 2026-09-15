// Decides which image tag a CD run deploys.
//
//   - an explicit IMAGE_TAG wins (deploy a specific build, or roll back to one);
//   - otherwise the tag currently running in the environment's `promoteFrom`
//     environment, read from the GitOps manifest or the ECS service.
//
// Promotion by reading what is actually deployed, rather than "latest" or the
// newest build, means prod receives exactly the artifact staging tested.
//
// Usage:
//   env.IMAGE_TAG = cdResolveImage(cfg, envCfg, params.IMAGE_TAG)
// Params: cfg (Map) - pipeline config; reads deployStrategy, imageRepo, gitopsRepo/gitopsBranch, environments
//         envCfg (Map) - target environment; reads name, promoteFrom
//         requestedTag (String) - tag asked for by the user; empty means "promote"
// Returns: the image tag to deploy; errors when none can be determined
def call(Map cfg, Map envCfg, String requestedTag) {
    def tag = requestedTag?.trim()
    if (tag) {
        if (!(tag ==~ /^[A-Za-z0-9_][A-Za-z0-9._-]{0,127}$/)) {
            error "IMAGE_TAG '${tag}' is not a valid image tag"
        }
        logInfo "Deploying requested tag ${tag} to ${envCfg.name}"
        return tag
    }

    if (!envCfg.promoteFrom) {
        error "IMAGE_TAG is required: ${envCfg.name} has no promoteFrom environment to take the image from"
    }

    def source = cfg.environments.find { it.name == envCfg.promoteFrom }
    def running = null

    switch (cfg.deployStrategy) {
        case 'gitops':
            def manifest = githubFetchFile(repo: cfg.gitopsRepo, branch: cfg.gitopsBranch, path: source.manifestPath)
            running = manifestCurrentImage(manifest, cfg.imageRepo)
            break

        case 'ecs':
            def container = source.ecsContainer ?: cfg.appName
            inToolContainer('amazon/aws-cli:latest') {
                withAwsCredentials(cfg, source) {
                    def taskDef = sh(
                        script: "aws ecs describe-services --cluster ${shellQuote(source.ecsCluster)} " +
                                "--services ${shellQuote(source.ecsService)} --query 'services[0].taskDefinition' --output text",
                        returnStdout: true
                    ).trim()
                    def image = sh(
                        script: "aws ecs describe-task-definition --task-definition ${shellQuote(taskDef)} " +
                                "--query \"taskDefinition.containerDefinitions[?name=='${container}'].image | [0]\" --output text",
                        returnStdout: true
                    ).trim()
                    running = manifestCurrentImage("image: ${image}", cfg.imageRepo)
                }
            }
            break

        default:
            error "cdResolveImage: promotion is not supported for deployStrategy '${cfg.deployStrategy}'"
    }

    if (!running) {
        error "Could not find a ${cfg.imageRepo} tag running in ${source.name} to promote"
    }
    if (!(running ==~ /^[A-Za-z0-9_][A-Za-z0-9._-]{0,127}$/)) {
        error "${source.name} runs '${running}', which is not a plain image tag; pass IMAGE_TAG explicitly"
    }

    logInfo "Promoting ${cfg.imageRepo}:${running} from ${source.name} to ${envCfg.name}"
    return running
}
