# standardPipeline

The single entry point every consuming repo calls. Everything else is
declared in `.ci/config.yaml`.

## Syntax

```groovy
@Library('platform-pipeline@main') _

standardPipeline()                    // reads .ci/config.yaml
standardPipeline(Map overrides)       // .ci/config.yaml + inline overrides
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `overrides` | `Map` | no | `[:]` | Config merged over `.ci/config.yaml`, same shape as the YAML. `configFile` picks a different file. See [configLoad](../other/configLoad.md). |

## Returns

Nothing. Declares and runs the whole declarative pipeline.

## Examples

**The usual Jenkinsfile:**

```groovy
@Library('platform-pipeline@main') _
standardPipeline()
```

with, for example:

```yaml
# .ci/config.yaml
appName: orders-api
buildTool: python
imageRepo: ghcr.io/acme/orders-api
gitopsRepo: acme/gitops-manifests
environments:
  - name: dev
    manifestPath: apps/orders-api/dev/kustomization.yaml
    branchPattern: "*"
  - name: prod
    manifestPath: apps/orders-api/prod/kustomization.yaml
    requiresApproval: true
    approvers: [platform-leads]
```

**Inline overrides:**

```groovy
standardPipeline([quality: [minCoverage: 90]])
```

**A second pipeline from the same repo** (e.g. a nightly job):

```groovy
standardPipeline(
    configFile: '.ci/nightly.yaml',
    quality   : [dependencyCheck: true],
    notify    : [on: 'always'],
)
```

**Pinning the library to a tag:**

```groovy
@Library('platform-pipeline@v2.3.0') _
standardPipeline()
```

## Stages

| Stage | Runs when | What runs |
|---|---|---|
| Init | always | [initPipeline](initPipeline.md) — checkout, load config, set version env vars; stashes the source |
| Lint | always ([lintApp](lintApp.md) skips itself if `lint.enabled: false`) | [lintApp](lintApp.md), then [archiveLintReports](archiveLintReports.md) |
| Build | always | [buildApp](buildApp.md) |
| Test | always | [testApp](testApp.md), then JUnit results ([appTestReport](appTestReport.md)) and [checkCoverage](checkCoverage.md) |
| Quality & Security | always | [qualityChecks](qualityChecks.md) in parallel |
| Package | always | [packageApp](packageApp.md), archive [appArtifacts](appArtifacts.md), [publishArtifact](publishArtifact.md) (apps only); if `containerize`: [buildImage](buildImage.md), [generateSbom](generateSbom.md), [signImage](signImage.md) |
| Scan Image | `containerize` and `quality.trivy` | [scanTrivy](scanTrivy.md) on the pushed image, then [postScanSummary](postScanSummary.md) |
| Deploy | the branch reaches at least one environment | one `Deploy: <env>` stage per environment from [configEnvironmentsFor](../other/configEnvironmentsFor.md), in order, each running [deployToEnvironment](../cloud/deployToEnvironment.md) |

On `main` with the config above: `… → Deploy: dev → Deploy: prod` (prod waits
for approval). On `feature/login`: `… → Deploy: dev` only.

## Pipeline options

| Option | Value |
|---|---|
| Agent label | `linux` for every stage |
| Timeout | 90 minutes |
| Build retention | 50 builds, artifacts for 10 |
| Concurrency | one build per branch; a new build aborts the previous one |
| Checkout | skipped by default; `initPipeline` checks out once and stashes |
| Timestamps | on |

## Post actions

| Result | Actions |
|---|---|
| always | archive `.ci-audit.jsonl`, clean the workspace |
| success | `ci/jenkins` status = success, [notifySlack](../other/notifySlack.md) `SUCCESS` |
| unstable | `ci/jenkins` status = failure, Slack `UNSTABLE` |
| failure | `ci/jenkins` status = failure, Slack `FAILURE` |
| aborted | `ci/jenkins` status = error |

!!! note
    Every stage runs on a fresh `linux` agent, unstashes the source
    (`unstash 'source'`), and wraps build steps in
    [inBuildContainer](inBuildContainer.md). Only the stash — not files
    created in earlier stages — carries across stages.

## Controller requirements

| Item | Needed for |
|---|---|
| agents labelled `linux` | every stage |
| `github-token` credential | commit statuses, PR comments, GitOps commits |
| `sonar-token` + `SONAR_HOST_URL` | `quality.sonar` |
| `ghcr-credentials` (or `REGISTRY_CREDENTIALS_ID`) | image push |
| `argocd-token` + `ARGOCD_SERVER` | gitops deploys |
| `slack-webhook` | `notify.slackChannel` |
| `github-token` with `contents: write` | `publish.githubRelease` |
| `aws-credentials` | Terraform / CloudFormation |
| `cosign-oidc-token` | `quality.signImage` |

## Source

[`vars/standardPipeline.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/standardPipeline.groovy)
