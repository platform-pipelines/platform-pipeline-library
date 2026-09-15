// Baseline every consuming repo inherits. Anything not set in .ci/config.yaml
// comes from here, so adding a new capability with a safe default does not
// require touching every repo.
//
// This map is also the schema: configUnknownKeys warns about any key in a
// repo's config that does not appear here, so every recognised key must be
// listed — with null when it has no default.
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
        // 'github' derives imageRepo as ghcr.io/<owner>/<repo> from the checkout
        imageRegistry  : null,
        containerize   : true,
        dockerfile     : 'Dockerfile',
        // see configImageBuilders
        imageBuilder   : 'kaniko-docker',
        gitopsRepo     : null,
        gitopsBranch   : 'main',
        // see configDeployStrategies — defaulted from buildTool
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
            sonar              : true,
            sonarProjectKey    : null,
            sonarSources       : '.',
            sonarExclusions    : '**/node_modules/**,**/target/**,**/build/**,**/dist/**',
            failOnQualityGate  : true,
            trivy              : true,
            trivyFailOn        : ['HIGH', 'CRITICAL'],
            trivyIgnoreUnfixed : true,
            secretScan         : true,
            dependencyCheck    : false,
            dependencyCheckCvss: 7,
            minCoverage        : null,
            sbom               : true,
            signImage          : false,
        ],
        publish        : [
            // Nexus repository for build artifacts; publishing is skipped when unset
            nexusRepo     : null,
            // true pushes build artifacts to GitHub Packages (GHCR) under the app's own repo
            githubPackages: false,
        ],
        approval       : [
            // true lets the person who triggered a build approve its deploy
            allowSelfApproval: false,
        ],
        notify         : [
            slackChannel: null,
            on          : 'change',
            githubChecks: true,
            emails      : [],
        ],
        // Free-form: never read by the pipeline, never checked for unknown keys.
        extra          : [:],
    ]
}
