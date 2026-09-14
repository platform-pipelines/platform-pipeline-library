# Cloud

AWS credentials, CloudFormation, Terraform, and the GitOps/Argo CD deploy
path. [`deployToEnvironment`](deployToEnvironment.md) is the router: it reads
`cfg.deployStrategy` (`gitops` | `terraform` | `cloudformation`) and calls the
right one of [deployGitops](deployGitops.md), [deployTerraform](deployTerraform.md),
[deployCloudFormation](deployCloudFormation.md) for each environment.

## AWS

- [assumeAwsRole](assumeAwsRole.md) — STS role assumption, exports temporary credentials.
- [withAwsCredentials](withAwsCredentials.md) — wraps a body with static keys or, if `assumeRole` is set, an assumed role.

## CloudFormation

Build-time: [cfnBuild](cfnBuild.md), [cfnPackage](cfnPackage.md), [cfnTest](cfnTest.md), [cfnLint](cfnLint.md), [cfnTemplates](cfnTemplates.md).

Deploy-time: [deployCloudFormation](deployCloudFormation.md) creates a change
set via [cfnChangeSet](cfnChangeSet.md), gates on it, then executes it via
[cfnDeploy](cfnDeploy.md) — the same review-then-apply principle as Terraform.

## Terraform

Build-time: [terraformBuild](terraformBuild.md), [terraformPackage](terraformPackage.md), [terraformTest](terraformTest.md), [terraformLint](terraformLint.md).

Deploy-time: [deployTerraform](deployTerraform.md) runs [terraformInit](terraformInit.md),
[terraformPlan](terraformPlan.md) (gated), then [terraformApply](terraformApply.md)
against the exact plan file that was reviewed.

## GitOps & deploy routing

- [deployGitops](deployGitops.md) bumps the image tag in a manifest repo and lets Argo CD reconcile.
- [deployToEnvironment](deployToEnvironment.md) — the per-environment strategy router described above.
- [argoSync](argoSync.md) — waits for Argo CD to report the new revision synced and healthy.
- [isInfraRepo](isInfraRepo.md), [configInfraTools](configInfraTools.md) — identify Terraform/CloudFormation repos, which skip containerization entirely (see [configLoad](../other/configLoad.md)).
