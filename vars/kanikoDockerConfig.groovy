// Kaniko authenticates from a docker config file — there is no daemon to
// run `docker login` against.
//
// The registry host is taken from cfg.imageRepo, so pushing to something other
// than GHCR (ECR, Harbor, a registry with a port) authenticates against the
// right host instead of silently sending credentials for ghcr.io.
//
// Usage:
//   kanikoDockerConfig(cfg)
// Params: cfg (Map) - pipeline config; cfg.imageRepo selects the registry host
// Writes /kaniko/.docker/config.json using the REGISTRY_CREDENTIALS_ID credential.
def call(Map cfg) {
    def repo  = cfg.imageRepo ?: ''
    def first = repo.contains('/') ? repo.substring(0, repo.indexOf('/')) : ''

    // Same rule docker uses: the first path segment is a registry host only if
    // it looks like one. Otherwise the image lives on Docker Hub.
    def isHost = first && (first.contains('.') || first.contains(':') || first == 'localhost')
    def host   = isHost ? first : 'https://index.docker.io/v1/'

    withCredentials([usernamePassword(
        credentialsId   : env.REGISTRY_CREDENTIALS_ID ?: 'ghcr-credentials',
        usernameVariable: 'REG_USER',
        passwordVariable: 'REG_PASS'
    )]) {
        withEnv(["REG_HOST=${host}"]) {
            sh '''
                mkdir -p /kaniko/.docker
                AUTH=$(printf "%s:%s" "$REG_USER" "$REG_PASS" | base64 -w0)
                printf '{"auths":{"%s":{"auth":"%s"}}}' "$REG_HOST" "$AUTH" > /kaniko/.docker/config.json
            '''
        }
    }
}
