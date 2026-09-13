// Applies the exact plan file that was reviewed and approved.
//
// No -var flags here on purpose: the saved plan already has every value baked
// in. Passing vars to `apply` with a plan file is an error in Terraform, and
// that strictness is a feature — it is what makes the approval meaningful.
def call(Map cfg, Map envCfg, String planFile) {
    logBanner "Apply: ${envCfg.name}"

    dir(cfg.infra.workingDir) {
        if (!fileExists(planFile)) {
            error "Plan file ${planFile} is gone. Apply must consume the approved plan, not a fresh one."
        }

        sh "terraform apply -input=false -no-color -auto-approve ${planFile}"

        sh 'terraform output -json > tf-outputs.json || true'
        archiveArtifacts artifacts: 'tf-outputs.json', allowEmptyArchive: true
    }

    logAudit('infra.apply', [
        environment: envCfg.name,
        summary    : env.TF_PLAN_SUMMARY,
        approver   : env.DEPLOY_APPROVER ?: logActor()
    ])
}
