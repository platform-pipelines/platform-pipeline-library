// Continuous delivery without a rebuild: deploy an image that standardPipeline
// already built, scanned and pushed, to one environment.
//
//     @Library('platform-pipeline@main') _
//     cdPipeline()
//
// Point a separate (non-multibranch) job at the app repo's Jenkinsfile.cd. It
// reads the same .ci/config.yaml, so environments, approvers and the deploy
// strategy are declared once.
//
//   ENVIRONMENT=prod                 promote what prod's `promoteFrom` runs
//   ENVIRONMENT=prod IMAGE_TAG=1.4.0 deploy that tag (also how you roll back)
//   DRY_RUN=true                     resolve and verify, change nothing
//
// Supports the image deploy strategies (gitops, ecs). Terraform and
// CloudFormation repos have no image to promote; their plan → approve → apply
// runs in standardPipeline.
//
// Usage:
//   cdPipeline()
//   cdPipeline([configFile: '.ci/config.yaml'])
// Params: overrides (Map) - inline config overrides merged over .ci/config.yaml (see configLoad)
// Returns: nothing; runs Resolve → Deploy for the chosen environment
def call(Map overrides = [:]) {

    def cfg
    def target

    pipeline {
        agent none

        parameters {
            string(name: 'ENVIRONMENT', defaultValue: '', trim: true,
                   description: 'Environment to deploy to — an environments[].name from .ci/config.yaml')
            string(name: 'IMAGE_TAG', defaultValue: '', trim: true,
                   description: "Image tag to deploy. Empty promotes whatever the environment's promoteFrom environment is running.")
            booleanParam(name: 'DRY_RUN', defaultValue: false,
                         description: 'Resolve the tag and check it exists in the registry, but deploy nothing')
        }

        options {
            timestamps()
            buildDiscarder(logRotator(numToKeepStr: '100'))
            // Queue, never abort: stopping a deploy halfway is worse than waiting.
            disableConcurrentBuilds()
            skipDefaultCheckout(true)
        }

        stages {

            stage('Resolve') {
                agent { label 'linux' }
                steps {
                    script {
                        logBanner 'CD: resolve'

                        def scmVars = checkout scm
                        if (scmVars?.GIT_COMMIT) { env.GIT_COMMIT = scmVars.GIT_COMMIT }

                        cfg = configLoad(overrides)

                        if (!(cfg.deployStrategy in ['gitops', 'ecs'])) {
                            error 'cdPipeline deploys container images (deployStrategy gitops or ecs). ' +
                                  "${cfg.deployStrategy} repos deploy through standardPipeline."
                        }

                        target = cfg.environments.find { it.name == params.ENVIRONMENT }
                        if (!target) {
                            error "ENVIRONMENT '${params.ENVIRONMENT}' is not declared in .ci/config.yaml. " +
                                  "Choose one of: ${cfg.environments*.name.join(', ')}"
                        }

                        env.APP_NAME      = cfg.appName
                        env.GIT_SHORT_SHA = versionShortSha()
                        env.IMAGE_TAG     = cdResolveImage(cfg, target, params.IMAGE_TAG)
                        // The tag is the version for approval prompts and commit messages.
                        env.APP_VERSION   = env.IMAGE_TAG

                        withRegistryAuth(cfg) {
                            imageDigest(cfg, env.IMAGE_TAG)
                        }

                        currentBuild.displayName = "#${env.BUILD_NUMBER} ${target.name} ← ${env.IMAGE_TAG}"
                        currentBuild.description = "${cfg.imageRepo}@${env.IMAGE_DIGEST}"

                        logAudit('cd.resolved', [
                            environment: target.name,
                            image      : "${cfg.imageRepo}:${env.IMAGE_TAG}",
                            digest     : env.IMAGE_DIGEST,
                            promoted   : !params.IMAGE_TAG,
                            dryRun     : params.DRY_RUN,
                        ])

                        if (params.DRY_RUN) {
                            logInfo "DRY_RUN: would deploy ${cfg.imageRepo}:${env.IMAGE_TAG} to ${target.name}"
                        }
                    }
                }
            }

            stage('Deploy') {
                when {
                    beforeAgent true
                    expression { !params.DRY_RUN }
                }
                agent { label 'linux' }
                steps {
                    script {
                        // deployToEnvironment runs the approval gate first when
                        // the environment requires one.
                        deployToEnvironment(cfg, target)
                    }
                }
            }
        }

        post {
            always {
                node('linux') {
                    archiveArtifacts artifacts: '.ci-audit.jsonl', allowEmptyArchive: true
                }
            }
            success {
                node('linux') {
                    script { notifySlack(cfg, 'SUCCESS') }
                }
            }
            failure {
                node('linux') {
                    script { notifySlack(cfg, 'FAILURE') }
                }
            }
            cleanup {
                node('linux') {
                    cleanWs(notFailBuild: true)
                }
            }
        }
    }
}
