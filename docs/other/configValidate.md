# configValidate

Returns a list of problems. Collects every error rather than throwing on the
first, so a misconfigured repo is fixed in one pass instead of five failed
builds.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Fully merged config to validate (see [`configLoad`](configLoad.md)). |

## Returns

`List` of human-readable problem strings; empty when `cfg` is valid.

## Usage

```groovy
def problems = configValidate(cfg)
```

## What it checks

- `appName` and `buildTool` are present, and `buildTool` is in [`configSupportedTools`](configSupportedTools.md).
- `containerize: true` requires `imageRepo`, and `imageBuilder` must be in [`configImageBuilders`](configImageBuilders.md).
- `quality.signImage` requires `containerize: true`.
- `deployStrategy` must be in [`configDeployStrategies`](configDeployStrategies.md), and `gitops` with declared environments requires `gitopsRepo`.
- Infrastructure repos (`buildTool` in [`configInfraTools`](../cloud/configInfraTools.md)) must have `containerize: false`.

!!! note "infra.region is required whenever CloudFormation is reachable"
    `buildTool: cloudformation` dispatches `cfnBuild`/`cfnPackage`
    independently of `deployStrategy`, and both paths call
    [`withAwsCredentials`](../cloud/withAwsCredentials.md). So `infra.region`
    is required when *either* `deployStrategy == 'cloudformation'` **or**
    `buildTool == 'cloudformation'` — checking `deployStrategy` alone would
    miss a config that explicitly overrides `deployStrategy` away from the
    value implied by `buildTool`.

- `notify.on` must be `always` / `failure` / `change`, `quality.minCoverage` (if set) must be between 0 and 100, and `quality.dependencyCheckCvss` must be a number between 0 and 10.
- Every environment needs a `name` (unique across the list); `gitops` environments also need `manifestPath`; `requiresApproval: true` needs a non-empty `approvers`.

Unknown keys are not validation errors — they are reported as warnings by
[`configUnknownKeys`](configUnknownKeys.md).

## Source

[`vars/configValidate.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configValidate.groovy)
