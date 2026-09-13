// Defaults applied to each entry under `environments:`.
def call() {
    [
        name                  : null,
        namespace             : null,
        manifestPath          : null,
        requiresApproval      : false,
        approvers             : [],
        branchPattern         : 'main',
        approvalTimeoutMinutes: 60,
        // infrastructure environments
        workspace             : null,   // terraform workspace
        backendConfig         : null,   // per-env backend file
        varFiles              : null,   // overrides infra.varFiles
        stackName             : null,   // cloudformation stack
        parameters            : [:],    // cloudformation parameters
        region                : null,
        assumeRole            : null,
        awsCredentialsId      : null,
    ]
}
