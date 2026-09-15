// Scheduled drift detection for a Terraform repo: plan every environment
// against real state and report any difference. Never applies.
//
//     @Library('platform-pipeline@main') _
//     terraformDriftPipeline(schedule: 'H 6 * * 1-5')
//
// Drift is someone changing infrastructure outside the pipeline (a console
// edit, an emergency CLI fix). Left alone, the next unrelated merge's plan
// quietly includes reverting it. Finding it the morning after is cheaper.
//
// Every declared environment is planned, regardless of branchPattern, using
// the same credentials, backend and policy checks as a deploy. A drifted
// environment marks the build UNSTABLE and notifies Slack; a failed plan or a
// policy failure marks it FAILURE.
//
// Usage:
//   terraformDriftPipeline()
//   terraformDriftPipeline(schedule: 'H */4 * * *', configFile: '.ci/config.yaml')
// Params: args.schedule (String) - cron spec for the trigger (default 'H 6 * * *'); '' disables the trigger
//         args.* - every other key is an inline config override (see configLoad)
// Returns: nothing
def call(Map args = [:]) {
    def overrides = [:] + args
    def schedule  = overrides.containsKey('schedule') ? overrides.remove('schedule') : 'H 6 * * *'

    def cfg
    def drifted = []

    pipeline {
        agent none

        triggers {
            cron(schedule)
        }

        options {
            timestamps()
            buildDiscarder(logRotator(numToKeepStr: '60'))
            timeout(time: 60, unit: 'MINUTES')
            disableConcurrentBuilds()
            skipDefaultCheckout(true)
        }

        stages {

            stage('Init') {
                agent { label 'linux' }
                steps {
                    script {
                        logBanner 'Drift detection'

                        def scmVars = checkout scm
                        if (scmVars?.GIT_COMMIT) { env.GIT_COMMIT = scmVars.GIT_COMMIT }

                        cfg = configLoad(overrides)
                        if (cfg.buildTool != 'terraform') {
                            error "terraformDriftPipeline supports buildTool: terraform (got ${cfg.buildTool})"
                        }
                        if (!cfg.environments) {
                            error 'No environments declared — nothing to check for drift'
                        }

                        env.APP_NAME      = cfg.appName
                        env.APP_VERSION   = versionResolve(env.BRANCH_NAME)
                        env.GIT_SHORT_SHA = versionShortSha()

                        stash name: 'source', useDefaultExcludes: false
                    }
                }
            }

            stage('Plan') {
                steps {
                    script {
                        cfg.environments.each { envCfg ->
                            stage("Drift: ${envCfg.name}") {
                                node('linux') {
                                    unstash 'source'
                                    inBuildContainer(cfg) {
                                        terraformPlan(cfg, envCfg)
                                    }
                                    if (env.TF_HAS_CHANGES == 'true') {
                                        drifted << "${envCfg.name}: ${env.TF_PLAN_SUMMARY}".toString()
                                        logWarn "DRIFT in ${envCfg.name}: ${env.TF_PLAN_SUMMARY}"
                                    } else {
                                        logInfo "${envCfg.name} matches the code"
                                    }
                                }
                            }
                        }

                        if (drifted) {
                            logAudit('infra.drift', [environments: drifted])
                            unstable "Drift detected in ${drifted.size()} environment(s):\n  ${drifted.join('\n  ')}"
                        }
                    }
                }
            }
        }

        post {
            unstable {
                node('linux') {
                    // Always announce drift, whatever notify.on says: an
                    // unchanged UNSTABLE result is still unresolved drift.
                    script { notifySlack(configMerge(cfg, [notify: [on: 'always']]), 'UNSTABLE') }
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
