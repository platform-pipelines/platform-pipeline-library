// Runs a body with registry credentials exported as DOCKER_CONFIG, and removes
// the credentials afterwards even when the body fails.
//
// Everything that talks to the image registry — kaniko, buildah, trivy image,
// cosign, crane — reads DOCKER_CONFIG, so one wrapper covers build, SBOM,
// signing, scanning and the CD pipeline's image lookup.
//
// Usage:
//   withRegistryAuth(cfg) { buildImage(cfg); generateSbom(cfg) }
// Params: cfg (Map) - pipeline config; cfg.imageRepo selects the registry (see kanikoDockerConfig)
//         body (Closure) - steps that pull from or push to the registry
// Returns: whatever body() returns
def call(Map cfg, Closure body) {
    def dockerConfig = kanikoDockerConfig(cfg)
    try {
        withEnv(["DOCKER_CONFIG=${dockerConfig}"]) {
            return body()
        }
    } finally {
        sh "rm -rf ${shellQuote(dockerConfig)}"
    }
}
