// Produces the plan that will be approved and applied.
//
// The plan is saved to a file and archived. That file — not a re-run of
// `terraform plan` — is what apply consumes. Re-planning after approval is
// the classic infrastructure pipeline bug: you approve plan A and apply
// plan B, because the world moved in between.
//
// Usage:
//   def planFile = terraformPlan(cfg, envCfg)
// Params: cfg (Map) - pipeline config; reads cfg.infra.workingDir/varFiles
//         envCfg (Map) - target environment config; envCfg.name/varFiles
// Returns: the plan file name (also archived as <name>.json/.txt); errors out if the plan itself fails
def call(Map cfg, Map envCfg) {
    logBanner "Plan: ${envCfg.name}"

    terraformInit(cfg, envCfg)

    def planFile = "tfplan-${envCfg.name}"
    def varFlags = (envCfg.varFiles ?: cfg.infra.varFiles ?: [])
                       .collect { "-var-file=${it}" }.join(' ')

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
    }

    return planFile
}
