// The single entrypoint every consuming repo calls.
//
//     @Library('platform-pipeline@main') _
//     standardPipeline()
//
// Everything else is declared in .ci/config.yaml.
//
// Usage:
//   standardPipeline()                       // reads .ci/config.yaml
//   standardPipeline([quality: [minCoverage: 90]])  // inline overrides
// Params: overrides (Map) - inline config overrides merged over .ci/config.yaml (see configLoad)
// Returns: nothing; runs the full Init/Lint/Build/Test/Quality/Package/Scan/Deploy pipeline
def call(Map overrides = [:]) {

    def cfg

    pipeline {
        agent none

        options {
            timestamps()
            buildDiscarder(logRotator(numToKeepStr: '50', artifactNumToKeepStr: '10'))
            timeout(time: 90, unit: 'MINUTES')
            disableConcurrentBuilds(abortPrevious: true)
            skipDefaultCheckout(true)
        }

        stages {

            stage('Init') {
                agent { label 'linux' }
                steps {
                    script {
                        cfg = initPipeline(overrides)
                        stash name: 'source', useDefaultExcludes: false
                    }
                }
            }

            stage('Lint') {
                agent { label 'linux' }
                steps {
                    script {
                        unstash 'source'
                        inBuildContainer(cfg) {
                            lintApp(cfg)
                        }
                    }
                }
                post {
                    always {
                        script { archiveLintReports(cfg) }
                    }
                }
            }

            stage('Build') {
                agent { label 'linux' }
                steps {
                    script {
                        unstash 'source'
                        inBuildContainer(cfg) {
                            buildApp(cfg)
                        }
                    }
                }
            }

            stage('Test') {
                agent { label 'linux' }
                steps {
                    script {
                        unstash 'source'
                        inBuildContainer(cfg) {
                            testApp(cfg)
                        }
                    }
                }
                post {
                    always {
                        script {
                            def reports = appTestReport(cfg)
                            if (reports) {
                                junit allowEmptyResults: true, testResults: reports
                            }
                            checkCoverage(cfg)
                        }
                    }
                }
            }

            stage('Quality & Security') {
                agent { label 'linux' }
                steps {
                    script {
                        unstash 'source'
                        parallel qualityChecks(cfg)
                    }
                }
            }

            stage('Package') {
                agent { label 'linux' }
                steps {
                    script {
                        unstash 'source'

                        inBuildContainer(cfg) {
                            packageApp(cfg)
                        }

                        def artifacts = appArtifacts(cfg)
                        if (artifacts) {
                            archiveArtifacts artifacts: artifacts,
                                             allowEmptyArchive: true,
                                             fingerprint: true
                        }

                        if (!isInfraRepo(cfg)) {
                            publishArtifact(cfg)
                        }

                        if (cfg.containerize) {
                            buildImage(cfg)
                            generateSbom(cfg)
                            signImage(cfg)
                        }
                    }
                }
            }

            stage('Scan Image') {
                when {
                    beforeAgent true
                    expression { cfg.containerize && cfg.quality.trivy }
                }
                agent { label 'linux' }
                steps {
                    script {
                        scanTrivy(cfg: cfg, target: "${cfg.imageRepo}:${env.IMAGE_TAG}", type: 'image')

                        postScanSummary(cfg, [
                            'Image scan': 'passed',
                            'Version'   : env.APP_VERSION,
                            'Digest'    : env.IMAGE_DIGEST ?: 'n/a',
                        ])
                    }
                }
            }

            stage('Deploy') {
                when {
                    beforeAgent true
                    expression { !configEnvironmentsFor(cfg, env.BRANCH_NAME).isEmpty() }
                }
                steps {
                    script {
                        // Sequential on purpose: dev has to go green before
                        // staging is even offered.
                        configEnvironmentsFor(cfg, env.BRANCH_NAME).each { envCfg ->
                            stage("Deploy: ${envCfg.name}") {
                                node('linux') {
                                    deployToEnvironment(cfg, envCfg)
                                }
                            }
                        }
                    }
                }
            }
        }

        post {
            always {
                node('linux') {
                    archiveArtifacts artifacts: '.ci-audit.jsonl', allowEmptyArchive: true
                    // Agents are long-lived, so a left-behind node_modules or
                    // .m2 eventually fills the disk and fails unrelated jobs.
                    cleanWs(notFailBuild: true)
                }
            }
            success {
                script {
                    githubSetStatus('ci/jenkins', 'success', "Passed in ${currentBuild.durationString}")
                    notifySlack(cfg, 'SUCCESS')
                }
            }
            unstable {
                script {
                    githubSetStatus('ci/jenkins', 'failure', 'Unstable — test failures')
                    notifySlack(cfg, 'UNSTABLE')
                }
            }
            failure {
                script {
                    githubSetStatus('ci/jenkins', 'failure', 'Pipeline failed')
                    notifySlack(cfg, 'FAILURE')
                }
            }
            aborted {
                script {
                    githubSetStatus('ci/jenkins', 'error', 'Aborted')
                }
            }
        }
    }
}
