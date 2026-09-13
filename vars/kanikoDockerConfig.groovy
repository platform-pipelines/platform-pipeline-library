// Kaniko authenticates from a docker config file — there is no daemon to
// run `docker login` against.
def call() {
    withCredentials([usernamePassword(
        credentialsId   : env.REGISTRY_CREDENTIALS_ID ?: 'ghcr-credentials',
        usernameVariable: 'REG_USER',
        passwordVariable: 'REG_PASS'
    )]) {
        sh '''
            mkdir -p /kaniko/.docker
            AUTH=$(printf "%s:%s" "$REG_USER" "$REG_PASS" | base64 -w0)
            printf '{"auths":{"ghcr.io":{"auth":"%s"}}}' "$AUTH" > /kaniko/.docker/config.json
        '''
    }
}
