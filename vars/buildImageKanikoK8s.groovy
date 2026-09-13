// Kaniko running as a sidecar container in the agent pod.
//
// This is the only genuinely unprivileged option: no docker socket anywhere.
// It requires the `kubernetes` plugin and a pod template that declares a
// container named 'kaniko' — see local/casc/jenkins.yaml for an example.
//
// Usage:
//   buildImageKanikoK8s(cfg)
// Params: cfg (Map) - pipeline config; passed through to kanikoArgs(cfg)
// Returns: nothing; runs kaniko in the pod's 'kaniko' sidecar container to build and push the image
def call(Map cfg) {
    container('kaniko') {
        sh "/kaniko/executor --context=dir://\$(pwd) ${kanikoArgs(cfg)}"
    }
}
