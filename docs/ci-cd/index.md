# CI/CD

Steps that build, test, lint, scan, and package an application repo, plus the
orchestration steps (`standardPipeline` and friends) that call them in the
right order. See [Cloud](../cloud/index.md) for the deploy-side steps
(CloudFormation, Terraform, GitOps).

Most steps take the loaded config as their only argument — `stepName(cfg)` —
and read the `.ci/config.yaml` keys listed on their page. The
[sample config](../index.md#sample-objects-used-throughout) on the home page
is what the examples assume.

## Language builds & tests

One `*Build` / `*Lint` / `*Package` / `*Test` step per supported `buildTool`.
`buildApp` / `lintApp` / `packageApp` / `testApp` dispatch to these by
`cfg.buildTool` — see [Pipeline orchestration](#pipeline-orchestration) below.

| Tool (`buildTool`) | Build | Lint | Package | Test |
|---|---|---|---|---|
| Go (`go`) | [goBuild](goBuild.md) | [goLint](goLint.md) | [goPackage](goPackage.md) | [goTest](goTest.md) |
| Gradle (`gradle`) | [gradleBuild](gradleBuild.md) | [gradleLint](gradleLint.md) | [gradlePackage](gradlePackage.md) | [gradleTest](gradleTest.md) |
| Maven (`maven`) | [mavenBuild](mavenBuild.md) | [mavenLint](mavenLint.md) | [mavenPackage](mavenPackage.md) | [mavenTest](mavenTest.md) |
| Node (`npm`) | [nodeBuild](nodeBuild.md) | [nodeLint](nodeLint.md) | [nodePackage](nodePackage.md) | [nodeTest](nodeTest.md) |
| Python (`python`) | [pythonBuild](pythonBuild.md) | [pythonLint](pythonLint.md) | [pythonPackage](pythonPackage.md) | [pythonTest](pythonTest.md) |
| docker-only (`docker-only`) | [dockerOnlyBuild](dockerOnlyBuild.md) | [dockerOnlyLint](dockerOnlyLint.md) | [dockerOnlyPackage](dockerOnlyPackage.md) | [dockerOnlyTest](dockerOnlyTest.md) |

Also: [gradleOpts](gradleOpts.md), [mavenOpts](mavenOpts.md), [nodeInstall](nodeInstall.md).

The lint keys every language step shares:

```yaml
lint:
  enabled: true        # false skips the Lint stage's work
  failOnError: true    # false = report-only
  autoFormat: false    # true = run the formatter's fix mode first
```

## Images & containers

- [buildImage](buildImage.md) dispatches to [buildImageBuildah](buildImageBuildah.md), [buildImageKanikoDocker](buildImageKanikoDocker.md), [buildImageKanikoK8s](buildImageKanikoK8s.md) by `cfg.imageBuilder`.
- [imageExtraTags](imageExtraTags.md), [imageLabels](imageLabels.md) — tag/label computation.
- [kanikoArgs](kanikoArgs.md), [kanikoDockerConfig](kanikoDockerConfig.md) — Kaniko build support.
- [inBuildContainer](inBuildContainer.md), [inContainer](inContainer.md), [inToolContainer](inToolContainer.md), [usingToolbox](usingToolbox.md) — where a step actually runs.
- [signImage](signImage.md), [generateSbom](generateSbom.md) — supply-chain signing and SBOMs.
- [publishArtifact](publishArtifact.md) → [publishArtifactNexus](publishArtifactNexus.md), [publishArtifactGithub](publishArtifactGithub.md) — build output to Nexus and/or GitHub Packages.

```yaml
imageRepo: ghcr.io/acme/orders-api   # or imageRegistry: github → ghcr.io/<owner>/<repo> of this checkout
dockerfile: Dockerfile
imageBuilder: kaniko-docker     # kaniko-docker | kaniko-k8s | buildah
quality:
  sbom: true
  signImage: true
```

## Quality & scanning

- [qualityChecks](qualityChecks.md) runs Sonar/Trivy/secret-scan/dependency-check in parallel per `cfg.quality`.
- Sonar: [scanSonar](scanSonar.md), [sonarProperties](sonarProperties.md), [sonarWaitForGate](sonarWaitForGate.md), [appSonarProps](appSonarProps.md).
- Trivy: [scanTrivy](scanTrivy.md), [trivySummary](trivySummary.md).
- [scanSecrets](scanSecrets.md), [scanIac](scanIac.md), [scanDependencies](scanDependencies.md).
- Coverage: [checkCoverage](checkCoverage.md), [coveragePercent](coveragePercent.md), [appCoverageFile](appCoverageFile.md).
- Reports: [appTestReport](appTestReport.md), [appLintReport](appLintReport.md), [lintApp](lintApp.md), [archiveLintReports](archiveLintReports.md).

```yaml
quality:
  sonar: true
  failOnQualityGate: true
  trivy: true
  trivyFailOn: [HIGH, CRITICAL]
  secretScan: true
  dependencyCheck: false
  minCoverage: 75
```

## Pipeline orchestration

- [standardPipeline](standardPipeline.md) is the entry point every consuming `Jenkinsfile` calls.
- [initPipeline](initPipeline.md) loads and validates config at the start of a run.
- [buildApp](buildApp.md), [lintApp](lintApp.md), [packageApp](packageApp.md), [testApp](testApp.md) dispatch by `buildTool`.
- [approvalGate](approvalGate.md) / [requestApproval](requestApproval.md) gate a deploy on a human.
- [postPlanSummary](postPlanSummary.md), [postScanSummary](postScanSummary.md) post PR comments.
- [publishArtifact](publishArtifact.md), [appArtifacts](appArtifacts.md), [appCacheDir](appCacheDir.md), [appToolImage](appToolImage.md) — build-tool metadata.
- [manifestBumpImage](manifestBumpImage.md), [updateManifest](updateManifest.md) — GitOps manifest rewriting.

## Quick reference

| Step | Syntax | Returns |
|---|---|---|
| [standardPipeline](standardPipeline.md) | `standardPipeline([quality: [minCoverage: 90]])` | — |
| [initPipeline](initPipeline.md) | `def cfg = initPipeline([:])` | config `Map` |
| [buildApp](buildApp.md) / [lintApp](lintApp.md) / [testApp](testApp.md) / [packageApp](packageApp.md) | `buildApp(cfg)` | — |
| [qualityChecks](qualityChecks.md) | `parallel qualityChecks(cfg)` | `Map` of closures |
| [scanTrivy](scanTrivy.md) | `scanTrivy(cfg: cfg, target: '.', type: 'fs')` | — |
| [buildImage](buildImage.md) | `buildImage(cfg)` | — (sets `IMAGE_REF`, `IMAGE_DIGEST`) |
| [approvalGate](approvalGate.md) | `approvalGate(cfg, envCfg)` | — (sets `DEPLOY_APPROVER`) |
| [updateManifest](updateManifest.md) | `updateManifest(cfg: cfg, env: envCfg, image: 'ghcr.io/acme/api:1.4.0')` | — |
| [manifestBumpImage](manifestBumpImage.md) | `manifestBumpImage(yamlText, 'ghcr.io/acme/api:1.4.0')` | updated `String` |
| [postScanSummary](postScanSummary.md) | `postScanSummary(cfg, [Trivy: 'clean'])` | — |
| [inBuildContainer](inBuildContainer.md) | `inBuildContainer(cfg) { sh 'make' }` | — |
| [inToolContainer](inToolContainer.md) | `inToolContainer('aquasec/trivy:latest') { … }` | value of the body |
| [appToolImage](appToolImage.md) | `appToolImage(cfg)` | e.g. `'python:3.12-slim'` |
| [coveragePercent](coveragePercent.md) | `coveragePercent(cfg)` | e.g. `82.4`, or `-1` |
