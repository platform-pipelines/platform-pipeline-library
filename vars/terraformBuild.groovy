// For Terraform there is nothing to compile — init plus validate against the
// real backend is the closest equivalent, and it catches backend and provider
// problems before the plan stage.
//
// Usage:
//   terraformBuild(cfg)
// Params: cfg (Map) - pipeline config; reads cfg.infra.workingDir
// Returns: nothing; runs terraform init then terraform validate
def call(Map cfg) {
    logBanner 'Build: Terraform'
    terraformInit(cfg)
    dir(cfg.infra.workingDir) {
        sh 'terraform validate -no-color'
    }
}
