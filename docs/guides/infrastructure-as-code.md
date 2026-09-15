# Infrastructure as code

`buildTool: terraform` or `cloudformation` switches the pipeline into an infra
shape. `containerize`, `deployStrategy`, SBOM and signing are all derived
([isInfraRepo](../reference/infrastructure/isInfraRepo.md)), so an IaC repo
does not restate them.

## What changes

- **Security scan**: `trivy config` over the templates instead of a
  dependency scan. Trivy absorbed tfsec, so this is one tool, not another one.
- **No image, no SBOM, no publish**: nothing is built.
- **Deploy**: plan, then approve, then apply.

## Apply consumes the exact approved plan

Terraform applies the saved `tfplan` file. CloudFormation executes the change
set created before the gate. Neither recomputes a diff after approval.

This prevents the classic infra pipeline bug: you approve plan A and apply
plan B, because the world moved in between. Both paths are built to make it
impossible rather than unlikely.

Plan summaries ([postPlanSummary](../reference/infrastructure/postPlanSummary.md))
call out destructive changes explicitly and post to the PR:

```
1 to create, 1 to update, 1 to replace, 1 to delete | DESTRUCTIVE: aws_db_instance.main, aws_security_group.old
```

## Terraform

[deployTerraform](../reference/infrastructure/deployTerraform.md) runs
[terraformInit](../reference/infrastructure/terraformInit.md),
[terraformPlan](../reference/infrastructure/terraformPlan.md), the approval gate, then
[terraformApply](../reference/infrastructure/terraformApply.md).
[terraformDriftPipeline](../reference/pipelines/terraformDriftPipeline.md) plans
every environment on a schedule and never applies.

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

## CloudFormation

[deployCloudFormation](../reference/infrastructure/deployCloudFormation.md) creates a
change set with [cfnChangeSet](../reference/infrastructure/cfnChangeSet.md), gates on
it, then executes it with [cfnDeploy](../reference/infrastructure/cfnDeploy.md).

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

## AWS credentials

[withAwsCredentials](../reference/infrastructure/withAwsCredentials.md) wraps each
deploy with static keys (`awsCredentialsId`) or, if `assumeRole` is set, an
assumed role ([assumeAwsRole](../reference/infrastructure/assumeAwsRole.md)). Both
can be set per repo (`infra.*`) or per environment.

```yaml
infra:
  region: eu-west-1
  awsCredentialsId: aws-credentials
environments:
  - name: prod
    assumeRole: arn:aws:iam::111122223333:role/JenkinsDeploy
```

## Next

[Images & supply chain](images-and-supply-chain.md)
