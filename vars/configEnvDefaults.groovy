// Defaults applied to each entry under `environments:`.
//
// Usage:
//   def defaults = configEnvDefaults()
// Returns: Map of default values for one `environments:` entry
def call() {
    [
        name                  : null,
        namespace             : null,
        manifestPath          : null,
        requiresApproval      : false,
        approvers             : [],
        branchPattern         : 'main',
        approvalTimeoutMinutes: 60,
        // cdPipeline: with no IMAGE_TAG given, deploy what this environment runs
        promoteFrom           : null,
        // deployStrategy: ecs
        ecsCluster            : null,
        ecsService            : null,
        ecsContainer          : null,   // container to update; defaults to appName
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
