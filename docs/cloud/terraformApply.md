# terraformApply

Applies the exact plan file that was reviewed and approved. No `-var` flags
here on purpose: the saved plan already has every value baked in. Passing
vars to `apply` with a plan file is an error in Terraform, and that
strictness is a feature — it's what makes the approval meaningful.

## Syntax

```groovy
terraformApply(Map cfg, Map envCfg, String planFile)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; reads `infra.workingDir`. |
| `envCfg` | `Map` | yes | — | Target environment; `name` is used for logs and the audit record. |
| `planFile` | `String` | yes | — | Plan file returned by [terraformPlan](terraformPlan.md), relative to `infra.workingDir`, e.g. `tfplan-prod`. |

Also reads `env.TF_PLAN_SUMMARY` and `env.DEPLOY_APPROVER` for the audit record.

## Returns

Nothing. Runs the apply, then writes and archives `tf-outputs.json`
(`terraform output -json`). Fails the build if the plan file is missing:

```
Plan file tfplan-prod is gone. Apply must consume the approved plan, not a fresh one.
```

## Examples

```groovy
def planFile = terraformPlan(cfg, envCfg)      // → 'tfplan-prod'
approvalGate(cfg, envCfg)
terraformApply(cfg, envCfg, planFile)
```

With `infra.workingDir: terraform`, runs inside `terraform/`:

```bash
terraform apply -input=false -no-color -auto-approve tfplan-prod
terraform output -json > tf-outputs.json
```

Audit record:

```
[AUDIT] infra.apply [environment:prod, summary:2 to create, 1 to update, approver:sam.lee]
```

Reading an output in a later step:

```groovy
dir('terraform') {
    def outputs = readJSON file: 'tf-outputs.json'
    echo "VPC: ${outputs.vpc_id.value}"
}
```

## How it fits

Called by [deployTerraform](deployTerraform.md) as the last step, after
[terraformPlan](terraformPlan.md) and an optional
[approvalGate](../ci-cd/approvalGate.md). Must run in the same workspace as
the plan (same stage/node).

## Source

[`vars/terraformApply.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/terraformApply.groovy)
