// Infra repos produce no build artifact — the plan is the artifact, and it is
// created and archived during deploy where it is bound to an environment.
//
// Usage:
//   terraformPackage(cfg)
// Params: cfg (Map) - pipeline config; unused, kept for call-signature consistency across buildTools
// Returns: nothing; logs that packaging is a no-op for Terraform
def call(Map cfg) {
    logInfo 'Terraform: the plan is the artifact, produced per environment at deploy time'
}
