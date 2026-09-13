// SonarQube analysis, then the quality gate if the config demands it.
def call(Map cfg) {
    logBanner 'SonarQube'

    def flags = sonarProperties(cfg)

    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
        inToolContainer('sonarsource/sonar-scanner-cli:latest') {
            sh "sonar-scanner -Dsonar.host.url=${env.SONAR_HOST_URL} -Dsonar.token=\$SONAR_TOKEN ${flags}"
        }
    }

    if (cfg.quality.failOnQualityGate) {
        sonarWaitForGate(cfg)
    }
}
