// Wraps a body with AWS credentials for the target environment.
//
// Prefers per-environment role assumption over static keys: a single set of
// long-lived keys that can reach production is the thing you least want on a
// build agent.
def call(Map cfg, Map envCfg = [:], Closure body) {
    def region = envCfg.region ?: cfg.infra.region
    def role   = envCfg.assumeRole ?: cfg.infra.assumeRole

    withEnv(["AWS_DEFAULT_REGION=${region}", "AWS_REGION=${region}"]) {
        withCredentials([usernamePassword(
            credentialsId   : envCfg.awsCredentialsId ?: cfg.infra.awsCredentialsId ?: 'aws-credentials',
            usernameVariable: 'AWS_ACCESS_KEY_ID',
            passwordVariable: 'AWS_SECRET_ACCESS_KEY'
        )]) {
            if (role) {
                assumeAwsRole(role) { body() }
            } else {
                body()
            }
        }
    }
}
