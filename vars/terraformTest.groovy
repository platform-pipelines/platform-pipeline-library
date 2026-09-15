// Native `terraform test`.
//
// Tests should use `mock_provider` (Terraform 1.7+) so they need no cloud
// credentials and no state: they check the module's logic, and run on every
// branch. Policy checks (infra.policyDir) are not run here — they need a real
// plan against real state, so terraformPlan runs them against each
// environment's plan, before that plan can be approved.
//
// Usage:
//   terraformTest(cfg)
// Params: cfg (Map) - pipeline config; reads cfg.infra.workingDir
// Returns: nothing; runs terraform test if tests exist, fails the build if a test fails
def call(Map cfg) {
    logBanner 'Test: Terraform'

    dir(cfg.infra.workingDir) {
        if (fileExists('tests') || sh(script: 'ls *.tftest.hcl 2>/dev/null | head -1', returnStdout: true).trim()) {
            // Each stage starts from the stash, so providers are not installed yet.
            sh 'terraform init -backend=false -input=false -no-color'
            sh 'terraform test -no-color'
        } else {
            logInfo 'No .tftest.hcl files — skipping terraform test'
        }
    }
}
