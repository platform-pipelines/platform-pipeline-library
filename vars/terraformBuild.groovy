// For Terraform there is nothing to compile — init plus validate against the
// real backend is the closest equivalent, and it catches backend and provider
// problems before the plan stage.
def call(Map cfg) {
    logBanner 'Build: Terraform'
    terraformInit(cfg)
    dir(cfg.infra.workingDir) {
        sh 'terraform validate -no-color'
    }
}
