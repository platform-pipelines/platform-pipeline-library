# configValidate

Returns a list of problems. Collects every error rather than throwing on the
first, so a misconfigured repo is fixed in one pass instead of five failed
builds.

## Syntax

```groovy
configValidate(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | **Fully merged** config to validate (defaults already applied — see [`configLoad`](configLoad.md)). |

## Returns

`List<String>` of human-readable problems; empty when `cfg` is valid. It never
fails the build itself — [`configLoad`](configLoad.md) does that when the list
is non-empty.

## What it checks

| Rule | Error message |
|---|---|
| `appName` present | `appName is required` |
| `buildTool` present and in [`configSupportedTools`](configSupportedTools.md) | `buildTool is required` / `buildTool 'x' unsupported (use: …)` |
| `containerize: true` needs `imageRepo` | `imageRepo is required when containerize is true` |
| `imageBuilder` in [`configImageBuilders`](configImageBuilders.md) (when containerized) | `imageBuilder 'x' unsupported (use: …)` |
| `quality.signImage` needs `containerize: true` | `quality.signImage requires containerize: true` |
| `deployStrategy` in [`configDeployStrategies`](configDeployStrategies.md) | `deployStrategy 'x' unsupported (use: …)` |
| gitops + environments needs `gitopsRepo` | `gitopsRepo is required when environments are declared with deployStrategy: gitops` |
| infra `buildTool` needs `containerize: false` | `containerize must be false for terraform` |
| CloudFormation needs `infra.region` | `infra.region is required for cloudformation` |
| `notify.on` is `always` / `failure` / `change` | `notify.on must be always, failure or change` |
| `quality.minCoverage` in 0–100 | `quality.minCoverage must be between 0 and 100` |
| `quality.dependencyCheckCvss` a number in 0–10 | `quality.dependencyCheckCvss must be a number between 0 and 10` |
| every environment has a `name` | `environments[0].name is required` |
| gitops environments have `manifestPath` | `environments[0].manifestPath is required for deployStrategy: gitops` |
| `requiresApproval: true` needs `approvers` | `environments[1] (prod) requires approval but lists no approvers` |
| environment names are unique | `environment 'dev' is declared more than once` |

!!! note "infra.region is required whenever CloudFormation is reachable"
    `buildTool: cloudformation` dispatches `cfnBuild`/`cfnPackage`
    independently of `deployStrategy`, and both paths call
    [`withAwsCredentials`](../cloud/withAwsCredentials.md). So `infra.region`
    is required when *either* `deployStrategy == 'cloudformation'` **or**
    `buildTool == 'cloudformation'` — checking `deployStrategy` alone would
    miss a config that explicitly overrides `deployStrategy` away from the
    value implied by `buildTool`.

## Examples

**Valid config:**

```groovy
def cfg = configMerge(configDefaults(), [appName: 'orders-api', buildTool: 'python',
                                         imageRepo: 'ghcr.io/acme/orders-api',
                                         deployStrategy: 'gitops'])
configValidate(cfg)      // → []
```

**Several problems at once:**

```yaml
appName: orders-api
buildTool: python          # containerize defaults to true, but no imageRepo
quality:
  minCoverage: 120
environments:
  - name: prod
    requiresApproval: true # no approvers, no manifestPath, no gitopsRepo
```

```groovy
configValidate(cfg)
// → [
//   'imageRepo is required when containerize is true',
//   'gitopsRepo is required when environments are declared with deployStrategy: gitops',
//   'quality.minCoverage must be between 0 and 100',
//   'environments[0].manifestPath is required for deployStrategy: gitops',
//   'environments[0] (prod) requires approval but lists no approvers',
// ]
```

Unknown keys are not validation errors — they are reported as warnings by
[`configUnknownKeys`](configUnknownKeys.md).

## Source

[`vars/configValidate.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configValidate.groovy)
