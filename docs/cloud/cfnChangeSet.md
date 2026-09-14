# cfnChangeSet

Creates a change set — CloudFormation's equivalent of a Terraform plan.
Returns the change set name, which [cfnDeploy](cfnDeploy.md) then executes.
Same principle as the Terraform path ([terraformPlan](terraformPlan.md) /
[terraformApply](terraformApply.md)): what gets executed is the thing that
was reviewed, not a freshly computed diff.

!!! note "Hardened against shell injection"
    `stack`, `changeSet`, `template`, and the CloudFormation tags are all
    shell-quoted before reaching `sh`. Parameter keys and capability names are
    validated against `^[A-Za-z0-9_-]+$` before use, and stack parameters are
    written to a JSON file (`cfn-params.json`) passed via `--parameters
    file://...` rather than built into a shell string by hand.

## Signature

```groovy
def call(Map cfg, Map envCfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.appName`, `cfg.infra.template`/`capabilities`. |
| `envCfg` | `Map` | Target environment config; reads `envCfg.name`/`stackName`/`parameters`. |

## Returns

The created change set name, or `null` if the stack already matches the
template (a no-op deploy).

## Usage

```groovy
def changeSet = cfnChangeSet(cfg, envCfg)
```

Called by [deployCloudFormation](deployCloudFormation.md), which passes the
result to [cfnDeploy](cfnDeploy.md) after an optional
[approvalGate](../ci-cd/approvalGate.md).

## Source

[`vars/cfnChangeSet.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/cfnChangeSet.groovy)
