// Kaniko run as a one-shot docker container on the agent.
//
// Works with the plugin set already pinned, which is why it is the default.
// The honest tradeoff: it needs a docker socket on the agent, so the agent is
// not truly unprivileged. If that matters, move to kaniko-k8s.
//
// `docker run`, not docker.image().inside(): inside() runs as the agent uid,
// and kaniko has to be root in its own container to unpack base image layers.
// It writes only image-digest.txt into the workspace, so the root-owned file
// is still removable by cleanWs.
//
// Usage:
//   buildImageKanikoDocker(cfg, dockerConfig)
// Params: cfg (Map) - pipeline config; passed through to kanikoArgs(cfg)
//         dockerConfig (String) - directory holding the registry config.json (from kanikoDockerConfig)
// Returns: nothing; runs kaniko in a one-shot docker container to build and push the image
def call(Map cfg, String dockerConfig) {
    sh """
        docker run --rm \\
          -v "\$WORKSPACE":/workspace \\
          -v ${shellQuote(dockerConfig)}:/kaniko/.docker:ro \\
          -w /workspace \\
          gcr.io/kaniko-project/executor:v1.23.2 \\
          --context=dir:///workspace ${kanikoArgs(cfg)}
    """
}
