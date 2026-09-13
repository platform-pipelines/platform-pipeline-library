// Kaniko run as a one-shot docker container on the agent.
//
// Works with the plugin set already pinned, which is why it is the default.
// The honest tradeoff: it needs a docker socket on the agent, so the agent is
// not truly unprivileged. If that matters, move to kaniko-k8s.
//
// Usage:
//   buildImageKanikoDocker(cfg)
// Params: cfg (Map) - pipeline config; passed through to kanikoArgs(cfg)
// Returns: nothing; runs kaniko in a one-shot docker container to build and push the image
def call(Map cfg) {
    docker.image('gcr.io/kaniko-project/executor:v1.23.2-debug')
          .inside("--entrypoint='' -v ${env.WORKSPACE}:/workspace") {
        sh "/kaniko/executor --context=dir:///workspace ${kanikoArgs(cfg)}"
    }
}
