// Baseline every consuming repo inherits. Anything not set in .ci/config.yaml
// comes from here, so adding a new capability with a safe default does not
// require touching every repo.
//
// Usage:
//   def defaults = configDefaults()
// Returns: Map of every recognized top-level config key with its default value
def call() {
    [
        appName        : null,
        buildTool      : null,
        runtimeVersion : null,
        imageRepo      : null,
        containerize   : true,
        dockerfile     : 'Dockerfile',
        // kaniko-docker | kaniko-k8s | buildah — see the buildImage* steps
        imageBuilder   : 'kaniko-docker',
        gitopsRepo     : null,
        gitopsBranch   : 'main',
        // gitops | terraform | cloudformation — defaulted from buildTool
        deployStrategy : null,
        infra          : [
            workingDir      : '.',
            varFiles        : [],
            backendConfig   : null,
            policyDir       : null,
            region          : 'us-east-1',
            assumeRole      : null,
            awsCredentialsId: 'aws-credentials',
            template        : 'template.yaml',
            templates       : null,
            artifactBucket  : null,
            capabilities    : ['CAPABILITY_IAM', 'CAPABILITY_NAMED_IAM'],
        ],
        environments   : [],
        lint           : [
            enabled    : true,
            failOnError: true,
            autoFormat : false,
        ],
        quality        : [
            sonar             : true,
            sonarProjectKey   : null,
            failOnQualityGate : true,
            trivy             : true,
            trivyFailOn       : ['HIGH', 'CRITICAL'],
            trivyIgnoreUnfixed: true,
            secretScan        : true,
            dependencyCheck   : false,
            minCoverage       : null,
            sbom              : true,
            signImage         : false,
        ],
        notify         : [
            slackChannel: null,
            on          : 'change',
            githubChecks: true,
            emails      : [],
        ],
        extra          : [:],
    ]
}
