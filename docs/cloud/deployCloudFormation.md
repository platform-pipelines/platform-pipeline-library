# deployCloudFormation

Infrastructure deploy for the CloudFormation strategy: create a change set,
gate on it, execute it. The gate sits between plan and apply so the approver
sees the actual diff — the same principle
[deployTerraform](deployTerraform.md) uses.

## Signature

```groovy
def call(Map cfg, Map envCfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config. |
| `envCfg` | `Map` | Target environment config; `envCfg.requiresApproval` gates on [approvalGate](../ci-cd/approvalGate.md). |

## Returns

Nothing — a no-op when the change set has no changes to apply.

## Usage

```groovy
deployCloudFormation(cfg, envCfg)
```

Called by [deployToEnvironment](deployToEnvironment.md) when
`cfg.deployStrategy == 'cloudformation'`. Internally: creates a change set
via [cfnChangeSet](cfnChangeSet.md), posts a plan summary, optionally gates
on approval, then executes via [cfnDeploy](cfnDeploy.md).

## Source

[`vars/deployCloudFormation.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/deployCloudFormation.groovy)
