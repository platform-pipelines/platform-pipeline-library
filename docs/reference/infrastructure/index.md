# Infrastructure

AWS credentials, Terraform and CloudFormation. For the plan → approve → apply
model and full sample configs, see the
[Infrastructure as code guide](../../guides/infrastructure-as-code.md).

## AWS

- [assumeAwsRole](assumeAwsRole.md): STS role assumption; exports temporary credentials.
- [withAwsCredentials](withAwsCredentials.md): wraps a body with static keys or, if `assumeRole` is set, an assumed role.

## Terraform

- **Build-time:** [terraformLint](terraformLint.md), [terraformBuild](terraformBuild.md), [terraformTest](terraformTest.md), [terraformPackage](terraformPackage.md).
- **Deploy-time:** [deployTerraform](deployTerraform.md) runs [terraformInit](terraformInit.md),
  [terraformPlan](terraformPlan.md) (gated), then [terraformApply](terraformApply.md)
  against the exact plan file that was reviewed.

## CloudFormation

- **Build-time:** [cfnLint](cfnLint.md), [cfnBuild](cfnBuild.md), [cfnTest](cfnTest.md), [cfnPackage](cfnPackage.md), [cfnTemplates](cfnTemplates.md).
- **Deploy-time:** [deployCloudFormation](deployCloudFormation.md) creates a change set with
  [cfnChangeSet](cfnChangeSet.md), gates on it, then executes it with [cfnDeploy](cfnDeploy.md).

## Shared

- [postPlanSummary](postPlanSummary.md): posts the plan/change-set summary to the PR.
- [isInfraRepo](isInfraRepo.md), [configInfraTools](configInfraTools.md): identify Terraform/CloudFormation repos, which skip containerization entirely.

## Quick reference

| Step | Syntax | Returns |
|---|---|---|
| [withAwsCredentials](withAwsCredentials.md) | `withAwsCredentials(cfg, envCfg) { … }` | — |
| [terraformPlan](terraformPlan.md) | `def plan = terraformPlan(cfg, envCfg)` | plan file name |
| [terraformApply](terraformApply.md) | `terraformApply(cfg, envCfg, plan)` | — |
| [cfnChangeSet](cfnChangeSet.md) | `def cs = cfnChangeSet(cfg, envCfg)` | change set name or `null` |
| [cfnDeploy](cfnDeploy.md) | `cfnDeploy(cfg, envCfg, cs)` | — |
| [cfnTemplates](cfnTemplates.md) | `cfnTemplates(cfg)` | `List` of template paths |
| [isInfraRepo](isInfraRepo.md) | `isInfraRepo(cfg)` | `boolean` |
