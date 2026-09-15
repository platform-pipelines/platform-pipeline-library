# terraformPlan

Produces the plan that will be approved and applied. The plan is saved to a
file and archived — that file, not a re-run of `terraform plan`, is what
[terraformApply](terraformApply.md) consumes. Re-planning after approval is
the classic infrastructure pipeline bug: you approve plan A and apply plan
B, because the world moved in between.

## Syntax

```groovy
def planFile = terraformPlan(Map cfg, Map envCfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |
| `envCfg` | `Map` | yes | — | One entry from `cfg.environments`. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `infra.workingDir` | `.` | `terraform` | Directory Terraform runs in. |
| `infra.varFiles` | `[]` | `[common.tfvars]` | `-var-file` flags when the environment sets none. |
| `environments[].varFiles` | `null` | `[common.tfvars, prod.tfvars]` | `-var-file` flags; **replaces** `infra.varFiles` (not added to it). |
| `environments[].name` | — | `prod` | Plan file name `tfplan-<name>`. |
| `environments[].workspace` / `backendConfig` | `null` | `prod` / `backends/prod.hcl` | Passed to [terraformInit](terraformInit.md). |

## Returns

The plan file name, e.g. `tfplan-prod` (inside `infra.workingDir`). Also:

| Output | Sample |
|---|---|
| `env.TF_HAS_CHANGES` | `true` / `false` |
| `env.TF_PLAN_SUMMARY` | `2 to create, 1 to update, 1 to delete \| DESTRUCTIVE: aws_security_group.old` |
| archived `tfplan-prod.json` | `terraform show -json` output |
| archived `tfplan-prod.txt` | human-readable plan |

Fails the build with `terraform plan failed for prod` if the plan errors.

## Examples

```yaml
# .ci/config.yaml — from examples/terraform-stack
appName: platform-network
buildTool: terraform
infra:
  workingDir: terraform
  varFiles: [common.tfvars]
environments:
  - name: dev
    workspace: dev
    backendConfig: backends/dev.hcl
    varFiles: [common.tfvars, dev.tfvars]
```

```groovy
def dev = cfg.environments.find { it.name == 'dev' }
def planFile = terraformPlan(cfg, dev)     // → 'tfplan-dev'

if (env.TF_HAS_CHANGES == 'true') {
    echo "Plan: ${env.TF_PLAN_SUMMARY}"
}
```

Runs inside `terraform/`:

```bash
terraform init -input=false -no-color -backend-config=backends/dev.hcl -reconfigure
terraform workspace select -or-create dev
terraform plan -input=false -no-color -detailed-exitcode -var-file=common.tfvars -var-file=dev.tfvars -out=tfplan-dev
terraform show -json tfplan-dev > tfplan-dev.json
terraform show -no-color tfplan-dev > tfplan-dev.txt
```

`-detailed-exitcode`: `0` = no changes, `2` = changes, `1` = error.

Summary line formats:

| Summary | Meaning |
|---|---|
| `no changes` | nothing to apply |
| `3 to create` | additive only |
| `1 to update, 1 to replace \| DESTRUCTIVE: aws_db_instance.main` | something is destroyed or recreated |

## How it fits

Called by [deployTerraform](deployTerraform.md), which checks
`env.TF_HAS_CHANGES` before posting a plan summary and gating on approval.
Summarised by the bundled `terraform_plan_summary.py` (see
[useScript](../utilities/useScript.md)).

## Source

[`vars/terraformPlan.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/terraformPlan.groovy)
