// Terraform lint: formatting, then validation, then tflint.
//
// fmt first because it is instant and catches the most common review comment.
//
// Usage:
//   terraformLint(cfg)
// Params: cfg (Map) - pipeline config; reads cfg.infra.workingDir, cfg.lint.autoFormat/failOnError
// Returns: nothing; errors out on a formatting/validation/tflint failure when cfg.lint.failOnError is true
def call(Map cfg) {
    logBanner 'Lint: Terraform'

    dir(cfg.infra.workingDir) {
        if (cfg.lint.autoFormat) {
            sh 'terraform fmt -recursive'
        }

        def fmtStatus = sh(script: 'terraform fmt -check -recursive -diff', returnStatus: true)

        // -backend=false so validation needs no credentials and no state access.
        // Validation is about syntax and types, not about what is deployed.
        sh 'terraform init -backend=false -input=false -no-color'
        sh 'terraform validate -no-color'

        def tflintStatus = 0
        if (fileExists('.tflint.hcl')) {
            sh 'tflint --init'
            tflintStatus = sh(
                script: 'tflint --format junit > tflint-report.xml 2>/dev/null; tflint --format compact',
                returnStatus: true
            )
        } else {
            logDebug 'No .tflint.hcl — skipping tflint'
        }

        if ((fmtStatus != 0 || tflintStatus != 0) && cfg.lint.failOnError) {
            error 'Terraform lint failed. Run: terraform fmt -recursive'
        }
    }
}
