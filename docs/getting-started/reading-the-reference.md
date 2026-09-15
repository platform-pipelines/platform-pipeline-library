# Reading the step reference

The [Step reference](../reference/index.md) has one page for every callable step in
[`vars/`](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/vars).
Steps are grouped by what they do, not alphabetically.

## Page layout

Every step page uses the same sections:

| Section | What it tells you |
|---|---|
| **Syntax** | Every way the step can be called, copy-pasteable. `[ ]` in a comment marks an optional argument. |
| **Parameters** | Each argument with its type, whether it is required, and its default. |
| **Config keys read** | For steps that take `cfg`: the `.ci/config.yaml` keys the step actually reads, their defaults, and a sample value. |
| **Returns** | The return value and any side effects (environment variables set, files archived, build failures). |
| **Examples** | Realistic calls with sample values and the result they produce. |

## Calling a step

Call steps from a `Jenkinsfile` or from inside `script { }` in a declarative
pipeline. Groovy lets you drop the parentheses for a single argument, so
`logInfo 'hi'` and `logInfo('hi')` are the same call. Named arguments
(`scanTrivy(cfg: cfg, type: 'fs')`) arrive as a single `Map`.

## Sample objects used throughout

Most steps take the loaded pipeline config (`cfg`). Deploy steps also take one
environment entry from it (`envCfg`). The examples on every page assume this
config:

```yaml
# .ci/config.yaml
appName: orders-api
buildTool: python
runtimeVersion: "3.12"
imageRepo: ghcr.io/acme/orders-api
gitopsRepo: acme/gitops-manifests

quality:
  minCoverage: 75

notify:
  slackChannel: "#orders-ci"

environments:
  - name: dev
    manifestPath: apps/orders-api/dev/kustomization.yaml
    branchPattern: "*"
  - name: prod
    manifestPath: apps/orders-api/prod/kustomization.yaml
    requiresApproval: true
    approvers: [platform-leads]
```

```groovy
def cfg    = configLoad()           // the whole merged config (defaults filled in)
def envCfg = cfg.environments[1]    // [name: 'prod', namespace: 'prod', requiresApproval: true, ...]
```

They also assume this build environment, set by Jenkins and
[`initPipeline`](../reference/pipelines/initPipeline.md):

| Variable | Sample value |
|---|---|
| `env.BRANCH_NAME` | `main` |
| `env.BUILD_NUMBER` | `42` |
| `env.GIT_COMMIT` | `ab12cd3ef4567890ab12cd3ef4567890ab12cd3e` |
| `env.GIT_SHORT_SHA` | `ab12cd3` |
| `env.APP_VERSION` | `1.4.0` |
| `env.IMAGE_TAG` | `1.4.0` |
| `env.CHANGE_ID` | unset on branch builds; the PR number (e.g. `87`) on PR builds |

Infrastructure pages (Terraform/CloudFormation) use the
[terraform-stack](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/terraform-stack)
and [cloudformation-stack](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/cloudformation-stack)
example configs instead, and say so.

## Next

Understand how a run fits together in the [Pipelines guide](../guides/pipelines.md).
