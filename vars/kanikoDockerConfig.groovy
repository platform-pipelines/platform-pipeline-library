// Registry credentials as a docker config file, for tools with no daemon to
// `docker login` against: kaniko, buildah, trivy, cosign and crane all read it
// through DOCKER_CONFIG.
//
// The registry host is taken from cfg.imageRepo, so pushing to something other
// than GHCR (ECR, Harbor, a registry with a port) authenticates against the
// right host instead of silently sending credentials for ghcr.io.
//
// ECR has no static password: for an *.dkr.ecr.<region>.amazonaws.com repo the
// token comes from `aws ecr get-login-password` under withAwsCredentials, so no
// registry credential is needed at all.
//
// The file lives in <workspace>@tmp, never in the workspace itself. The
// workspace is the kaniko build context, and a Dockerfile with `COPY . .`
// would otherwise bake the registry password into the image.
//
// Usage:
//   def dockerConfig = kanikoDockerConfig(cfg)
//   withEnv(["DOCKER_CONFIG=${dockerConfig}"]) { sh 'trivy image ...' }
// Params: cfg (Map) - pipeline config; cfg.imageRepo selects the registry host
// Returns: the directory holding config.json (set DOCKER_CONFIG to it)
def call(Map cfg) {
    def repo  = cfg.imageRepo ?: ''
    def first = repo.contains('/') ? repo.substring(0, repo.indexOf('/')) : ''

    // Same rule docker uses: the first path segment is a registry host only if
    // it looks like one. Otherwise the image lives on Docker Hub.
    def isHost = first && (first.contains('.') || first.contains(':') || first == 'localhost')
    def host   = isHost ? first : 'https://index.docker.io/v1/'
    def dir    = "${env.WORKSPACE}@tmp/registry-auth".toString()

    def writeConfig = '''
        mkdir -p "$REG_DIR"
        umask 077
        AUTH=$(printf "%s:%s" "$REG_USER" "$REG_PASS" | base64 | tr -d '\\n')
        printf '{"auths":{"%s":{"auth":"%s"}}}' "$REG_HOST" "$AUTH" > "$REG_DIR/config.json"
    '''

    def ecr = host =~ /^\d{12}\.dkr\.ecr\.([a-z0-9-]+)\.amazonaws\.com$/
    if (ecr.find()) {
        def region = ecr.group(1)
        // Container outside, credentials inside: assumeAwsRole runs `aws sts`,
        // which has to happen where the AWS CLI is.
        inToolContainer('amazon/aws-cli:latest') {
            withAwsCredentials(cfg, [region: region]) {
                withEnv(["REG_HOST=${host}", "REG_DIR=${dir}", 'REG_USER=AWS']) {
                    sh "REG_PASS=\$(aws ecr get-login-password --region ${region}); export REG_PASS; ${writeConfig}"
                }
            }
        }
        return dir
    }

    withCredentials([usernamePassword(
        credentialsId   : env.REGISTRY_CREDENTIALS_ID ?: 'ghcr-credentials',
        usernameVariable: 'REG_USER',
        passwordVariable: 'REG_PASS'
    )]) {
        withEnv(["REG_HOST=${host}", "REG_DIR=${dir}"]) {
            sh writeConfig
        }
    }
    return dir
}
