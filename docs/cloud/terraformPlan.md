# terraformPlan

Produces the plan that will be approved and applied. The plan is saved to a
file and archived — that file, not a re-run of `terraform plan`, is what
[terraformApply](terraformApply.md) consumes. Re-planning after approval is
the classic infrastructure pipeline bug: you approve plan A and apply plan
B, because the world moved in between.

## Signature

```groovy
def call(Map cfg, Map envCfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.infra.workingDir`/`varFiles`. |
| `envCfg` | `Map` | Target environment config; reads `envCfg.name`/`varFiles`. |

## Returns

The plan file name (also archived as `<name>.json`/`.txt`). Errors out if
the plan itself fails.

## Usage

```groovy
def planFile = terraformPlan(cfg, envCfg)
```

Called by [deployTerraform](deployTerraform.md), which checks
`env.TF_HAS_CHANGES` before posting a plan summary and gating on approval.

## Source

[`vars/terraformPlan.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/terraformPlan.groovy)
