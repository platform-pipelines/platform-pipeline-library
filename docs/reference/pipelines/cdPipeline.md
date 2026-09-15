# cdPipeline

Continuous delivery without a rebuild: deploy an image that
[standardPipeline](standardPipeline.md) already built, scanned and pushed, to
one environment. Promote what the previous environment runs, deploy a specific
tag, or roll back.

## Syntax

```groovy
@Library('platform-pipeline@main') _

cdPipeline()                    // reads .ci/config.yaml
cdPipeline(Map overrides)       // .ci/config.yaml + inline overrides
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `overrides` | `Map` | no | `[:]` | Config merged over `.ci/config.yaml`. See [configLoad](../config/configLoad.md). |

### Build parameters

| Parameter | Default | Effect |
|---|---|---|
| `ENVIRONMENT` | `''` | Environment to deploy — an `environments[].name`. Required. |
| `IMAGE_TAG` | `''` | Tag to deploy. Empty promotes the tag running in the environment's `promoteFrom`. |
| `DRY_RUN` | `false` | Resolve the tag and check the registry, deploy nothing. |

### Config keys read

| Key | Sample value | Used by |
|---|---|---|
| `deployStrategy` | `gitops` / `ecs` | anything else is rejected — infra repos deploy through standardPipeline |
| `imageRepo` | `ghcr.io/acme/inventory-api` | [imageDigest](../images/imageDigest.md), deploy |
| `environments[].promoteFrom` | `staging` | [cdResolveImage](../deploy/cdResolveImage.md) |
| `environments[].requiresApproval` / `approvers` | `true` / `[platform-leads]` | [approvalGate](../deploy/approvalGate.md) |

## Returns

Nothing. Declares and runs the pipeline.

## Examples

```groovy
// Jenkinsfile.cd — a plain Pipeline job, branch main
@Library('platform-pipeline@main') _
cdPipeline()
```

```yaml
environments:
  - name: staging
    manifestPath: apps/api/staging/kustomization.yaml
    branchPattern: main
  - name: prod
    manifestPath: apps/api/prod/kustomization.yaml
    branchPattern: none          # CI never deploys prod; only the CD job does
    promoteFrom: staging
    requiresApproval: true
    approvers: [platform-leads]
```

| Run with | Result |
|---|---|
| `ENVIRONMENT=prod` | reads the tag staging runs (`1.4.0`), checks it exists, asks for approval, deploys |
| `ENVIRONMENT=prod IMAGE_TAG=1.3.2` | deploys `1.3.2` — a rollback |
| `ENVIRONMENT=prod DRY_RUN=true` | logs `DRY_RUN: would deploy ghcr.io/acme/api:1.4.0 to prod` |

## Stages

| Stage | Runs when | What runs |
|---|---|---|
| Resolve | always | checkout, [configLoad](../config/configLoad.md), [cdResolveImage](../deploy/cdResolveImage.md), [withRegistryAuth](../images/withRegistryAuth.md) + [imageDigest](../images/imageDigest.md); build named `#12 prod ← 1.4.0` |
| Deploy | not `DRY_RUN` | [deployToEnvironment](../deploy/deployToEnvironment.md) (approval, then GitOps bump + Argo sync, or [deployEcs](../deploy/deployEcs.md)) |

Concurrent runs queue rather than abort: stopping a deploy halfway is worse
than waiting. The first run of a new job only registers the parameters.

See [examples/cd-promotion](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/cd-promotion).

## Source

[`vars/cdPipeline.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/cdPipeline.groovy)
