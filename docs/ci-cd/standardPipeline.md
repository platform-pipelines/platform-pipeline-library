# standardPipeline

The single entry point every consuming repo calls:

```groovy
@Library('platform-pipeline@main') _
standardPipeline()
```

Everything else is declared in `.ci/config.yaml`.

## Signature

```groovy
def call(Map overrides = [:])
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `overrides` | `Map` | Inline config overrides merged over `.ci/config.yaml` (see [configLoad](../other/configLoad.md)). |

## Returns

Nothing. Runs the full pipeline for the calling repo.

## Usage

```groovy
standardPipeline()                              // reads .ci/config.yaml
standardPipeline([quality: [minCoverage: 90]])  // inline overrides
```

## Stages

| Stage | What runs |
|---|---|
| Init | [initPipeline](initPipeline.md) — checkout, load config, set version env vars |
| Lint | [lintApp](lintApp.md), then archives reports via [archiveLintReports](archiveLintReports.md) |
| Build | [buildApp](buildApp.md) |
| Test | [testApp](testApp.md), then JUnit results and [checkCoverage](checkCoverage.md) |
| Quality & Security | [qualityChecks](qualityChecks.md) in parallel |
| Package | [packageApp](packageApp.md), [publishArtifact](publishArtifact.md), and — if `cfg.containerize` — `buildImage`, `generateSbom`, `signImage` (see [Images & containers](index.md#images-containers)) |
| Scan Image | `scanTrivy` against the built image, when containerized and `cfg.quality.trivy` |
| Deploy | one [deployToEnvironment](../cloud/deployToEnvironment.md) stage per environment the current branch reaches, run sequentially |

`post` always archives the audit log and cleans the workspace, then reports
build status via `githubSetStatus` and [notifySlack](../other/notifySlack.md).

!!! note
    Every stage re-checks out the stashed source (`unstash 'source'`) and
    runs inside [inBuildContainer](inBuildContainer.md) — agents are
    ephemeral between stages, so nothing but the stash and the audit log
    file survives across them.

## Source

[`vars/standardPipeline.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/standardPipeline.groovy)
