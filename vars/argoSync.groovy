// Waits for Argo CD to converge.
//
// Argo would sync on its own, but blocking here means a green build actually
// means "the deploy landed", which is what people assume it means.
def call(Map cfg, Map envCfg) {
    def app = "${cfg.appName}-${envCfg.name}"

    logBanner "Argo CD: ${app}"

    withCredentials([string(credentialsId: 'argocd-token', variable: 'ARGOCD_TOKEN')]) {
        inToolContainer('quay.io/argoproj/argocd:latest') {
            def argo = "argocd --server ${env.ARGOCD_SERVER} --auth-token \$ARGOCD_TOKEN --grpc-web"

            timeout(time: 10, unit: 'MINUTES') {
                sh "${argo} app wait ${app} --health --sync --timeout 600"
            }

            def status = sh(script: "${argo} app get ${app} -o wide", returnStdout: true).trim()
            logInfo status
            logAudit('deploy.synced', [app: app, environment: envCfg.name])
        }
    }
}
