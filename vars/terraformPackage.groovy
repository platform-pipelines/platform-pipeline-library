// Infra repos produce no build artifact — the plan is the artifact, and it is
// created and archived during deploy where it is bound to an environment.
def call(Map cfg) {
    logInfo 'Terraform: the plan is the artifact, produced per environment at deploy time'
}
