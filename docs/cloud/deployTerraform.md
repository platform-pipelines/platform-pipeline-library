# deployTerraform

Infrastructure deploy: plan, gate on the plan, apply that exact plan. The
approval sits between plan and apply deliberately — an approver is approving
a specific set of changes, not a general intention to deploy.

## Signature

```groovy
def call(Map cfg, Map envCfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config. |
| `envCfg` | `Map` | Target environment config (`name`, `requiresApproval`, ...). |

## Returns

Nothing — applies the plan, or skips if there are no changes.

## Usage

```groovy
deployTerraform(cfg, envCfg)
```

Called by [deployToEnvironment](deployToEnvironment.md) when
`cfg.deployStrategy == 'terraform'`. Internally: [terraformPlan](terraformPlan.md),
a plan summary post, an optional [approvalGate](../ci-cd/approvalGate.md),
then [terraformApply](terraformApply.md).

## Source

[`vars/deployTerraform.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/deployTerraform.groovy)
