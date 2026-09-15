// Kaniko running as a sidecar container in the agent pod.
//
// This is the only genuinely unprivileged option: no docker socket anywhere.
// It requires the `kubernetes` plugin and a pod template that declares a
// container named 'kaniko' — see local/casc/jenkins.yaml for an example. The
// workspace volume (and its @tmp sibling holding the registry config) is shared
// between the pod's containers, so DOCKER_CONFIG resolves in the sidecar too.
//
// Usage:
//   buildImageKanikoK8s(cfg, dockerConfig)
// Params: cfg (Map) - pipeline config; passed through to kanikoArgs(cfg)
//         dockerConfig (String) - directory holding the registry config.json (from kanikoDockerConfig)
// Returns: nothing; runs kaniko in the pod's 'kaniko' sidecar container to build and push the image
def call(Map cfg, String dockerConfig) {
    container('kaniko') {
        withEnv(["DOCKER_CONFIG=${dockerConfig}"]) {
            sh "/kaniko/executor --context=dir://\$(pwd) ${kanikoArgs(cfg)}"
        }
    }
}
