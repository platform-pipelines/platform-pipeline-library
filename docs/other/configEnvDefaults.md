# configEnvDefaults

Defaults applied to each entry under `environments:` — the per-environment
counterpart to [`configDefaults`](configDefaults.md).

## Signature

```groovy
def call()
```

## Returns

`Map` of default values for one `environments:` entry — `name`, `namespace`,
`manifestPath`, `requiresApproval`, `approvers`, `branchPattern`,
`approvalTimeoutMinutes`, plus the infrastructure-only fields (`workspace`,
`backendConfig`, `stackName`, `parameters`, `region`, `assumeRole`,
`awsCredentialsId`).

## Usage

```groovy
def defaults = configEnvDefaults()
```

[`configLoad`](configLoad.md) merges this under each declared environment via
[`configMerge`](configMerge.md), then falls back `namespace` to the
environment's `name` if it wasn't set.

## Source

[`vars/configEnvDefaults.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configEnvDefaults.groovy)
