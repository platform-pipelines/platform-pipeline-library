// For Terraform there is nothing to compile — init plus validate is the
// closest equivalent, and it catches provider and module problems before the
// plan stage.
//
// -backend=false on purpose. The real backend is per environment (its own
// backendConfig, workspace and credentials), so it is initialised at plan time
// inside that environment's credential scope. Initialising it here would need
// AWS credentials in the Build stage and would pick one environment's state
// for a check that is about the code, not about any deployment.
//
// Usage:
//   terraformBuild(cfg)
// Params: cfg (Map) - pipeline config; reads cfg.infra.workingDir
// Returns: nothing; runs terraform init -backend=false then terraform validate
def call(Map cfg) {
    logBanner 'Build: Terraform'
    dir(cfg.infra.workingDir) {
        sh 'terraform init -backend=false -input=false -no-color'
        sh 'terraform validate -no-color'
    }
}
