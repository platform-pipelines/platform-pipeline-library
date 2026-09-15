# Pipelines

Entry points that a `Jenkinsfile` calls, plus the steps that run stages in
order and dispatch on `cfg.buildTool`. For a narrative overview, see the
[Pipelines guide](../../guides/pipelines.md).

## Entry points

- [standardPipeline](standardPipeline.md): CI and deploy for a multibranch job.
- [cdPipeline](cdPipeline.md): promote or roll back an already-built image.
- [terraformDriftPipeline](terraformDriftPipeline.md): scheduled Terraform drift detection.
- [initPipeline](initPipeline.md): loads and validates config at the start of a run.

## Dispatchers

[lintApp](lintApp.md), [buildApp](buildApp.md), [testApp](testApp.md) and
[packageApp](packageApp.md) route to the [language steps](../languages/index.md)
by `cfg.buildTool`, and fail loudly on an unknown value.

## Build-tool metadata

These steps switch on the same `buildTool` value:
[appToolImage](appToolImage.md), [appTestReport](appTestReport.md),
[appCoverageFile](appCoverageFile.md), [appArtifacts](appArtifacts.md),
[appCacheDir](appCacheDir.md), [appLintReport](appLintReport.md),
[appSonarProps](appSonarProps.md).

## Quick reference

| Step | Syntax | Returns |
|---|---|---|
| [standardPipeline](standardPipeline.md) | `standardPipeline([quality: [minCoverage: 90]])` | — |
| [initPipeline](initPipeline.md) | `def cfg = initPipeline([:])` | config `Map` |
| [buildApp](buildApp.md) / [lintApp](lintApp.md) / [testApp](testApp.md) / [packageApp](packageApp.md) | `buildApp(cfg)` | — |
| [appToolImage](appToolImage.md) | `appToolImage(cfg)` | e.g. `'python:3.12-slim'` |
