// Application deploy to Amazon ECS: register a task definition revision with
// the new image, roll the service onto it, wait until it is stable, and roll
// back to the previous revision if it never gets there.
//
// The infrastructure (cluster, service, the task definition's roles, sizing
// and logging) belongs to Terraform or CloudFormation. This step only ever
// changes the container image, by copying the service's current revision.
// Configure the service with `deployment_circuit_breaker { rollback = true }`
// too: ECS then stops a bad rollout on its own, and this step's rollback is
// the backstop for rollouts that are merely stuck.
//
// Usage:
//   deployEcs(cfg, envCfg)
// Params: cfg (Map) - pipeline config; reads cfg.imageRepo, cfg.appName, cfg.infra (credentials)
//         envCfg (Map) - target environment; reads ecsCluster, ecsService, ecsContainer (default appName)
// Returns: nothing; errors (after rolling back) if the service does not become stable
def call(Map cfg, Map envCfg) {
    def image     = "${cfg.imageRepo}:${env.IMAGE_TAG}".toString()
    def container = envCfg.ecsContainer ?: cfg.appName
    def cluster   = shellQuote(envCfg.ecsCluster)
    def service   = shellQuote(envCfg.ecsService)

    logBanner "ECS: ${envCfg.ecsCluster}/${envCfg.ecsService} -> ${image}"

    inToolContainer('amazon/aws-cli:latest') {
        withAwsCredentials(cfg, envCfg) {
            def previous = sh(
                script: "aws ecs describe-services --cluster ${cluster} --services ${service} " +
                        "--query 'services[0].taskDefinition' --output text",
                returnStdout: true
            ).trim()
            if (!previous || previous == 'None') {
                error "ECS service ${envCfg.ecsService} not found in cluster ${envCfg.ecsCluster}"
            }

            sh "aws ecs describe-task-definition --task-definition ${shellQuote(previous)} " +
               '--query taskDefinition --output json > ecs-taskdef-previous.json'

            def current = readJSON(file: 'ecs-taskdef-previous.json', returnPojo: true)
            def next = ecsTaskDefinitionForImage(current, container, image)
            if (next == null) {
                error "Task definition ${previous} has no container named '${container}' " +
                      "(found: ${current.containerDefinitions*.name.join(', ')}). Set ecsContainer for ${envCfg.name}."
            }
            writeJSON file: 'ecs-taskdef.json', json: next, pretty: 2

            def revision = sh(
                script: 'aws ecs register-task-definition --cli-input-json file://ecs-taskdef.json ' +
                        '--query taskDefinition.taskDefinitionArn --output text',
                returnStdout: true
            ).trim()
            logInfo "Registered ${revision}"

            sh "aws ecs update-service --cluster ${cluster} --service ${service} " +
               "--task-definition ${shellQuote(revision)} > /dev/null"

            // services-stable polls every 15s for up to 10 minutes.
            def stable = sh(
                script: "aws ecs wait services-stable --cluster ${cluster} --services ${service}",
                returnStatus: true
            ) == 0

            archiveArtifacts artifacts: 'ecs-taskdef*.json', allowEmptyArchive: true

            if (!stable) {
                logError "${envCfg.ecsService} did not become stable on ${revision} — rolling back to ${previous}"
                sh "aws ecs describe-services --cluster ${cluster} --services ${service} " +
                   "--query 'services[0].events[:10].[createdAt,message]' --output table || true"
                sh "aws ecs update-service --cluster ${cluster} --service ${service} " +
                   "--task-definition ${shellQuote(previous)} > /dev/null"
                def restored = sh(
                    script: "aws ecs wait services-stable --cluster ${cluster} --services ${service}",
                    returnStatus: true
                ) == 0
                logAudit('deploy.rollback', [environment: envCfg.name, failed: revision, restored: previous, stable: restored])
                error "Deploy of ${image} to ${envCfg.name} failed; rolled back to ${previous}" +
                      (restored ? '' : ' (the rollback did not stabilise either — check the service now)')
            }

            logAudit('deploy', [
                environment   : envCfg.name,
                image         : image,
                cluster       : envCfg.ecsCluster,
                service       : envCfg.ecsService,
                taskDefinition: revision,
                approver      : env.DEPLOY_APPROVER ?: logActor(),
            ])
            logInfo "${envCfg.ecsService} is stable on ${revision}"
        }
    }
}
