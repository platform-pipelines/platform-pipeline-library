// Kaniko running as a sidecar container in the agent pod.
//
// This is the only genuinely unprivileged option: no docker socket anywhere.
// It requires the `kubernetes` plugin and a pod template that declares a
// container named 'kaniko' — see local/casc/jenkins.yaml for an example.
def call(Map cfg) {
    container('kaniko') {
        sh "/kaniko/executor --context=dir://\$(pwd) ${kanikoArgs(cfg)}"
    }
}
