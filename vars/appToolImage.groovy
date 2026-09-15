// Container the build runs in.
//
// When the agent is the toolbox image (CI_TOOLBOX=true) every toolchain is
// already on PATH, so steps run in place and this is never consulted.
//
// Otherwise, when the controller sets CI_TOOLBOX_IMAGE (see local/casc), the
// build runs inside that image. That is the recommended setup: the stock
// per-language images below have the compiler but not the linters and scanners
// the steps call (golangci-lint, ruff, tflint, conftest, cfn-lint, checkov,
// python3 for the plan summaries), so they only get a repo partway through.
//
// Usage:
//   def image = appToolImage(cfg)
// Params: cfg (Map) - pipeline config; reads cfg.buildTool and cfg.runtimeVersion
// Returns: container image for cfg.buildTool (errors if buildTool is unsupported)
def call(Map cfg) {
    if (env.CI_TOOLBOX_IMAGE) {
        return env.CI_TOOLBOX_IMAGE
    }

    def v = cfg.runtimeVersion
    switch (cfg.buildTool) {
        case 'go':          return "golang:${v ?: '1.27'}"
        case 'python':      return "python:${v ?: '3.12'}-slim"
        case 'maven':       return "maven:3.9-eclipse-temurin-${v ?: '21'}"
        case 'gradle':      return "gradle:9-jdk${v ?: '21'}"
        case 'npm':         return "node:${v ?: '24'}-alpine"
        case 'terraform':      return "hashicorp/terraform:${v ?: '1.16'}"
        case 'cloudformation': return 'amazon/aws-cli:latest'
        case 'docker-only': return 'alpine:3.19'
        default: error "No tool image for buildTool '${cfg.buildTool}'"
    }
}
