# terraformApply

Applies the exact plan file that was reviewed and approved. No `-var` flags
here on purpose: the saved plan already has every value baked in. Passing
vars to `apply` with a plan file is an error in Terraform, and that
strictness is a feature — it's what makes the approval meaningful.

## Signature

```groovy
def call(Map cfg, Map envCfg, String planFile)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.infra.workingDir`. |
| `envCfg` | `Map` | Target environment config; `envCfg.name` for logging. |
| `planFile` | `String` | Path to the previously approved plan file. |

## Returns

Nothing — errors out if `planFile` no longer exists.

## Usage

```groovy
terraformApply(cfg, envCfg, planFile)
```

Called by [deployTerraform](deployTerraform.md) as the last step, after
[terraformPlan](terraformPlan.md) and an optional
[approvalGate](../ci-cd/approvalGate.md).

## Source

[`vars/terraformApply.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/terraformApply.groovy)
