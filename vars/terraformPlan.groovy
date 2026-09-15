// Produces the plan that will be approved and applied.
//
// The plan is saved to a file and archived. That file — not a re-run of
// `terraform plan` — is what apply consumes. Re-planning after approval is
// the classic infrastructure pipeline bug: you approve plan A and apply
// plan B, because the world moved in between.
//
// When infra.policyDir exists, conftest checks this plan before it is returned,
// so a plan that breaks policy never reaches an approver.
//
// Usage:
//   def planFile = terraformPlan(cfg, envCfg)
// Params: cfg (Map) - pipeline config; reads cfg.infra.workingDir/varFiles/policyDir
//         envCfg (Map) - target environment config; envCfg.name/varFiles, plus credentials keys (see withAwsCredentials)
// Returns: the plan file name (also archived as <name>.json/.txt); errors if the plan or the policy check fails
def call(Map cfg, Map envCfg) {
    logBanner "Plan: ${envCfg.name}"

    def planFile = "tfplan-${envCfg.name}"
    def varFlags = (envCfg.varFiles ?: cfg.infra.varFiles ?: [])
                       .collect { "-var-file=${shellQuote(it as String)}" }.join(' ')

    // init, plan and show all read state and call provider APIs, so the whole
    // thing runs with this environment's credentials and assumed role.
    withAwsCredentials(cfg, envCfg) {
        terraformInit(cfg, envCfg)

        dir(cfg.infra.workingDir) {
            // detailed-exitcode: 0 = no changes, 1 = error, 2 = changes present.
            def status = sh(
                script: "terraform plan -input=false -no-color -detailed-exitcode ${varFlags} -out=${planFile}",
                returnStatus: true
            )

            if (status == 1) {
                error "terraform plan failed for ${envCfg.name}"
            }

            env.TF_HAS_CHANGES = (status == 2) ? 'true' : 'false'

            sh "terraform show -json ${planFile} > ${planFile}.json"
            sh "terraform show -no-color ${planFile} > ${planFile}.txt"

            archiveArtifacts artifacts: "${planFile}.json,${planFile}.txt", allowEmptyArchive: false

            def summary = sh(
                script: "python3 ${useScript('terraform_plan_summary.py')} ${planFile}.json",
                returnStdout: true
            ).trim()

            env.TF_PLAN_SUMMARY = summary
            logInfo "Plan: ${summary}"
            logAudit('infra.plan', [environment: envCfg.name, summary: summary])

            def policyDir = cfg.infra.policyDir
            if (policyDir && fileExists(policyDir)) {
                def policyStatus = sh(
                    script: "conftest test --no-color --policy ${shellQuote(policyDir)} ${planFile}.json",
                    returnStatus: true
                )
                if (policyStatus != 0) {
                    logAudit('infra.policy_failed', [environment: envCfg.name, policyDir: policyDir])
                    error "Policy check failed for the ${envCfg.name} plan — see the conftest output above"
                }
                logInfo "Policy check passed (${policyDir})"
            }
        }
    }

    return planFile
}
