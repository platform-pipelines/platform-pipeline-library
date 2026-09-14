# deployTerraform

Infrastructure deploy: plan, gate on the plan, apply that exact plan. The
approval sits between plan and apply deliberately — an approver is approving
a specific set of changes, not a general intention to deploy.

## Syntax

```groovy
deployTerraform(Map cfg, Map envCfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |
| `envCfg` | `Map` | yes | — | One entry from `cfg.environments`. |

### Config keys read

| Key | Default | Sample value | Used by |
|---|---|---|---|
| `infra.workingDir` | `.` | `terraform` | plan, apply |
| `infra.varFiles` | `[]` | `[common.tfvars]` | plan (when the environment sets none) |
| `infra.backendConfig` | `null` | `backends/default.hcl` | init (when the environment sets none) |
| `environments[].name` | — | `prod` | plan file name `tfplan-prod` |
| `environments[].workspace` | `null` | `prod` | init |
| `environments[].backendConfig` | `null` | `backends/prod.hcl` | init |
| `environments[].varFiles` | `null` | `[common.tfvars, prod.tfvars]` | plan |
| `environments[].requiresApproval` | `false` | `true` | gate |
| `environments[].approvers` | `[]` | `[platform-leads]` | gate |
| `environments[].approvalTimeoutMinutes` | `60` | `240` | gate |

## Returns

Nothing. Applies the plan, or logs `No changes for prod — skipping apply` and
returns when the plan is empty.

## Examples

```yaml
# .ci/config.yaml — from examples/terraform-stack
appName: platform-network
buildTool: terraform
infra:
  workingDir: terraform
  varFiles: [common.tfvars]
environments:
  - name: prod
    workspace: prod
    backendConfig: backends/prod.hcl
    varFiles: [common.tfvars, prod.tfvars]
    requiresApproval: true
    approvers: [platform-leads]
    approvalTimeoutMinutes: 240
```

```groovy
deployTerraform(cfg, cfg.environments[0])
```

What happens:

| Step | Detail |
|---|---|
| 1. [terraformPlan](terraformPlan.md) | `terraform plan -var-file=common.tfvars -var-file=prod.tfvars -out=tfplan-prod` |
| 2. no changes? | stop here |
| 3. [postPlanSummary](../ci-cd/postPlanSummary.md) | PR comment: `**prod**: 2 to create, 1 to update` |
| 4. [approvalGate](../ci-cd/approvalGate.md) | Jenkins input *Deploy platform-network 1.4.0 to prod?*, 240 min timeout |
| 5. [terraformApply](terraformApply.md) | `terraform apply … tfplan-prod` — the plan that was approved |

## How it fits

Called by [deployToEnvironment](deployToEnvironment.md) when
`cfg.deployStrategy == 'terraform'`.

## Source

[`vars/deployTerraform.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/deployTerraform.groovy)
