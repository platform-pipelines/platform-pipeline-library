# CI/CD

Steps that build, test, lint, scan, and package an application repo, plus the
orchestration steps (`standardPipeline` and friends) that call them in the
right order. See [Cloud](../cloud/index.md) for the deploy-side steps
(CloudFormation, Terraform, GitOps).

## Language builds & tests

One `*Build` / `*Lint` / `*Package` / `*Test` step per supported `buildTool`.
`buildApp` / `lintApp` / `packageApp` / `testApp` dispatch to these by
`cfg.buildTool` — see [Pipeline orchestration](#pipeline-orchestration) below.

| Tool | Build | Lint | Package | Test |
|---|---|---|---|---|
| Go | [goBuild](goBuild.md) | [goLint](goLint.md) | [goPackage](goPackage.md) | [goTest](goTest.md) |
| Gradle | [gradleBuild](gradleBuild.md) | [gradleLint](gradleLint.md) | [gradlePackage](gradlePackage.md) | [gradleTest](gradleTest.md) |
| Maven | [mavenBuild](mavenBuild.md) | [mavenLint](mavenLint.md) | [mavenPackage](mavenPackage.md) | [mavenTest](mavenTest.md) |
| Node | [nodeBuild](nodeBuild.md) | [nodeLint](nodeLint.md) | [nodePackage](nodePackage.md) | [nodeTest](nodeTest.md) |
| Python | [pythonBuild](pythonBuild.md) | [pythonLint](pythonLint.md) | [pythonPackage](pythonPackage.md) | [pythonTest](pythonTest.md) |
| docker-only | [dockerOnlyBuild](dockerOnlyBuild.md) | [dockerOnlyLint](dockerOnlyLint.md) | [dockerOnlyPackage](dockerOnlyPackage.md) | [dockerOnlyTest](dockerOnlyTest.md) |

Also: [gradleOpts](gradleOpts.md), [mavenOpts](mavenOpts.md), [nodeInstall](nodeInstall.md).

## Images & containers

- [buildImage](buildImage.md) dispatches to [buildImageBuildah](buildImageBuildah.md), [buildImageKanikoDocker](buildImageKanikoDocker.md), [buildImageKanikoK8s](buildImageKanikoK8s.md) by `cfg.imageBuilder`.
- [imageExtraTags](imageExtraTags.md), [imageLabels](imageLabels.md) — tag/label computation.
- [kanikoArgs](kanikoArgs.md), [kanikoDockerConfig](kanikoDockerConfig.md) — Kaniko build support.
- [inBuildContainer](inBuildContainer.md), [inContainer](inContainer.md), [inToolContainer](inToolContainer.md), [usingToolbox](usingToolbox.md) — where a step actually runs.
- [signImage](signImage.md), [generateSbom](generateSbom.md) — supply-chain signing and SBOMs.

## Quality & scanning

- [qualityChecks](qualityChecks.md) runs Sonar/Trivy/secret-scan/dependency-check/coverage per `cfg.quality`.
- Sonar: [scanSonar](scanSonar.md), [sonarProperties](sonarProperties.md), [sonarWaitForGate](sonarWaitForGate.md), [appSonarProps](appSonarProps.md).
- Trivy: [scanTrivy](scanTrivy.md), [trivySummary](trivySummary.md).
- [scanSecrets](scanSecrets.md), [scanIac](scanIac.md), [scanDependencies](scanDependencies.md).
- Coverage: [checkCoverage](checkCoverage.md), [coveragePercent](coveragePercent.md), [appCoverageFile](appCoverageFile.md).
- Reports: [appTestReport](appTestReport.md), [appLintReport](appLintReport.md), [lintApp](lintApp.md), [archiveLintReports](archiveLintReports.md).

## Pipeline orchestration

- [standardPipeline](standardPipeline.md) is the entry point every consuming `Jenkinsfile` calls.
- [initPipeline](initPipeline.md) loads and validates config at the start of a run.
- [buildApp](buildApp.md), [packageApp](packageApp.md), [testApp](testApp.md) dispatch by `buildTool`.
- [approvalGate](approvalGate.md) / [requestApproval](requestApproval.md) gate a deploy on a human.
- [postPlanSummary](postPlanSummary.md), [postScanSummary](postScanSummary.md) post PR comments.
- [publishArtifact](publishArtifact.md), [appArtifacts](appArtifacts.md), [appCacheDir](appCacheDir.md), [appToolImage](appToolImage.md) — build-tool metadata.
- [manifestBumpImage](manifestBumpImage.md), [updateManifest](updateManifest.md) — GitOps manifest rewriting.
