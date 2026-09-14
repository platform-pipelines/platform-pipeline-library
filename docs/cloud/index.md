# Cloud

AWS credentials, CloudFormation, Terraform, and the GitOps/Argo CD deploy
path. [`deployToEnvironment`](deployToEnvironment.md) is the router: it reads
`cfg.deployStrategy` (`gitops` | `terraform` | `cloudformation`) and calls the
right one of [deployGitops](deployGitops.md), [deployTerraform](deployTerraform.md),
[deployCloudFormation](deployCloudFormation.md) for each environment.

## Quick reference

| Step | Syntax | Returns |
|---|---|---|
| [assumeAwsRole](assumeAwsRole.md) | `assumeAwsRole('arn:aws:iam::123456789012:role/deploy') { … }` | — |
| [withAwsCredentials](withAwsCredentials.md) | `withAwsCredentials(cfg, envCfg) { … }` | — |
| [cfnTemplates](cfnTemplates.md) | `cfnTemplates(cfg)` | `List` of template paths |
| [cfnLint](cfnLint.md) / [cfnBuild](cfnBuild.md) / [cfnTest](cfnTest.md) / [cfnPackage](cfnPackage.md) | `cfnLint(cfg)` | — |
| [cfnChangeSet](cfnChangeSet.md) | `def cs = cfnChangeSet(cfg, envCfg)` | change set name or `null` |
| [cfnDeploy](cfnDeploy.md) | `cfnDeploy(cfg, envCfg, cs)` | — |
| [deployCloudFormation](deployCloudFormation.md) | `deployCloudFormation(cfg, envCfg)` | — |
| [terraformInit](terraformInit.md) | `terraformInit(cfg, envCfg)` | — |
| [terraformLint](terraformLint.md) / [terraformBuild](terraformBuild.md) / [terraformTest](terraformTest.md) / [terraformPackage](terraformPackage.md) | `terraformLint(cfg)` | — |
| [terraformPlan](terraformPlan.md) | `def plan = terraformPlan(cfg, envCfg)` | plan file name |
| [terraformApply](terraformApply.md) | `terraformApply(cfg, envCfg, plan)` | — |
| [deployTerraform](deployTerraform.md) | `deployTerraform(cfg, envCfg)` | — |
| [deployGitops](deployGitops.md) | `deployGitops(cfg, envCfg)` | — |
| [argoSync](argoSync.md) | `argoSync(cfg, envCfg)` | — |
| [deployToEnvironment](deployToEnvironment.md) | `deployToEnvironment(cfg, envCfg)` | — |
| [isInfraRepo](isInfraRepo.md) | `isInfraRepo(cfg)` | `boolean` |
| [configInfraTools](configInfraTools.md) | `configInfraTools()` | `['terraform', 'cloudformation']` |

## AWS

- [assumeAwsRole](assumeAwsRole.md) — STS role assumption, exports temporary credentials.
- [withAwsCredentials](withAwsCredentials.md) — wraps a body with static keys or, if `assumeRole` is set, an assumed role.

```yaml
infra:
  region: eu-west-1
  awsCredentialsId: aws-credentials
environments:
  - name: prod
    assumeRole: arn:aws:iam::111122223333:role/JenkinsDeploy
```

## CloudFormation

Build-time: [cfnBuild](cfnBuild.md), [cfnPackage](cfnPackage.md), [cfnTest](cfnTest.md), [cfnLint](cfnLint.md), [cfnTemplates](cfnTemplates.md).

Deploy-time: [deployCloudFormation](deployCloudFormation.md) creates a change
set via [cfnChangeSet](cfnChangeSet.md), gates on it, then executes it via
[cfnDeploy](cfnDeploy.md) — the same review-then-apply principle as Terraform.

```yaml
appName: billing-stack
buildTool: cloudformation
infra:
  template: templates/root.yaml
  artifactBucket: acme-cfn-artifacts
  region: us-east-1
environments:
  - name: prod
    stackName: billing-prod
    parameters: {Environment: prod}
    requiresApproval: true
    approvers: [platform-leads]
```

## Terraform

Build-time: [terraformBuild](terraformBuild.md), [terraformPackage](terraformPackage.md), [terraformTest](terraformTest.md), [terraformLint](terraformLint.md).

Deploy-time: [deployTerraform](deployTerraform.md) runs [terraformInit](terraformInit.md),
[terraformPlan](terraformPlan.md) (gated), then [terraformApply](terraformApply.md)
against the exact plan file that was reviewed.

```yaml
appName: platform-network
buildTool: terraform
infra:
  workingDir: terraform
  varFiles: [common.tfvars]
  policyDir: policies
environments:
  - name: prod
    workspace: prod
    backendConfig: backends/prod.hcl
    varFiles: [common.tfvars, prod.tfvars]
    requiresApproval: true
    approvers: [platform-leads]
```

## GitOps & deploy routing

- [deployGitops](deployGitops.md) bumps the image tag in a manifest repo and lets Argo CD reconcile.
- [deployToEnvironment](deployToEnvironment.md) — the per-environment strategy router described above.
- [argoSync](argoSync.md) — waits for Argo CD to report the new revision synced and healthy.
- [isInfraRepo](isInfraRepo.md), [configInfraTools](configInfraTools.md) — identify Terraform/CloudFormation repos, which skip containerization entirely (see [configLoad](../other/configLoad.md)).

```yaml
imageRepo: ghcr.io/acme/orders-api
gitopsRepo: acme/gitops-manifests
environments:
  - name: prod
    manifestPath: apps/orders-api/prod/kustomization.yaml   # Argo CD app: orders-api-prod
```
