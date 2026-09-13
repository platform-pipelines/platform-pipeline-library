// terraform init with the environment's backend configuration.
//
// -input=false everywhere: an interactive prompt in CI hangs until the build
// times out, which looks like a stuck agent rather than a config error.
//
// Usage:
//   terraformInit(cfg, envCfg)
// Params: cfg (Map) - pipeline config; reads cfg.infra.workingDir/backendConfig
//         envCfg (Map) - target environment config; envCfg.backendConfig/workspace override cfg's (default [:])
// Returns: nothing; runs terraform init and selects/creates the workspace if given
def call(Map cfg, Map envCfg = [:]) {
    def dir = cfg.infra.workingDir
    def backend = envCfg.backendConfig ?: cfg.infra.backendConfig

    def flags = '-input=false -no-color'
    if (backend) {
        flags += " -backend-config=${backend}"
    }

    dir(dir) {
        sh "terraform init ${flags} -reconfigure"

        if (envCfg.workspace) {
            // select-or-create; `workspace select` alone fails on first run
            sh "terraform workspace select -or-create ${envCfg.workspace}"
        }
    }
}
