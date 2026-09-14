# configEnvDefaults

Defaults applied to each entry under `environments:` — the per-environment
counterpart to [`configDefaults`](configDefaults.md).

## Syntax

```groovy
configEnvDefaults()
```

## Parameters

None.

## Returns

A `Map` of default values for one `environments:` entry:

| Key | Default | Used by | Sample value |
|---|---|---|---|
| `name` | `null` (required) | all | `prod` |
| `namespace` | `null` → falls back to `name` | gitops | `orders-prod` |
| `manifestPath` | `null` (required for gitops) | gitops | `apps/orders-api/prod/kustomization.yaml` |
| `requiresApproval` | `false` | all | `true` |
| `approvers` | `[]` (required when `requiresApproval`) | all | `[platform-leads, jane.doe]` |
| `branchPattern` | `main` | all | `"*"`, `release/*` |
| `approvalTimeoutMinutes` | `60` | all | `240` |
| `workspace` | `null` | terraform | `prod` |
| `backendConfig` | `null` (falls back to `infra.backendConfig`) | terraform | `backends/prod.hcl` |
| `varFiles` | `null` (falls back to `infra.varFiles`) | terraform | `[common.tfvars, prod.tfvars]` |
| `stackName` | `null` → `<appName>-<name>` | cloudformation | `billing-prod` |
| `parameters` | `[:]` | cloudformation | `{Environment: prod, InstanceType: m6i.large}` |
| `region` | `null` (falls back to `infra.region`) | terraform, cloudformation | `eu-west-1` |
| `assumeRole` | `null` (falls back to `infra.assumeRole`) | terraform, cloudformation | `arn:aws:iam::111122223333:role/JenkinsDeploy` |
| `awsCredentialsId` | `null` (falls back to `infra.awsCredentialsId`) | terraform, cloudformation | `aws-prod-credentials` |

## Examples

```groovy
def defaults = configEnvDefaults()
defaults.branchPattern            // → 'main'
defaults.approvalTimeoutMinutes   // → 60
```

What a short entry becomes after [`configLoad`](configLoad.md) merges it:

```yaml
# .ci/config.yaml
environments:
  - name: staging
    manifestPath: apps/orders-api/staging/kustomization.yaml
```

```groovy
cfg.environments[0]
// → [name: 'staging', namespace: 'staging',
//    manifestPath: 'apps/orders-api/staging/kustomization.yaml',
//    requiresApproval: false, approvers: [], branchPattern: 'main',
//    approvalTimeoutMinutes: 60, workspace: null, ... parameters: [:], ...]
```

## How it fits

[`configLoad`](configLoad.md) merges this under each declared environment via
[`configMerge`](configMerge.md), then falls back `namespace` to the
environment's `name` if it wasn't set. [`configUnknownKeys`](configUnknownKeys.md)
uses its keys to spot typos inside `environments[]` entries.

## Source

[`vars/configEnvDefaults.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configEnvDefaults.groovy)
