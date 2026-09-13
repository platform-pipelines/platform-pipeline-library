// Native `terraform test` plus OPA policy checks over the plan.
//
// Policy checks belong here rather than in the security stage because a
// policy failure is a logic error in the change, not a vulnerability.
def call(Map cfg) {
    logBanner 'Test: Terraform'

    dir(cfg.infra.workingDir) {
        if (fileExists('tests') || sh(script: 'ls *.tftest.hcl 2>/dev/null | head -1', returnStdout: true).trim()) {
            sh 'terraform test -no-color'
        } else {
            logInfo 'No .tftest.hcl files — skipping terraform test'
        }

        if (cfg.infra.policyDir && fileExists(cfg.infra.policyDir)) {
            sh 'terraform plan -input=false -no-color -out=policy.tfplan'
            sh 'terraform show -json policy.tfplan > policy-plan.json'
            sh "conftest test --policy ${cfg.infra.policyDir} policy-plan.json"
            sh 'rm -f policy.tfplan policy-plan.json'
        }
    }
}
