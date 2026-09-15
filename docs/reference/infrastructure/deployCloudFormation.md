# deployCloudFormation

Infrastructure deploy for the CloudFormation strategy: create a change set,
gate on it, execute it. The gate sits between plan and apply so the approver
sees the actual diff — the same principle
[deployTerraform](deployTerraform.md) uses.

## Syntax

```groovy
deployCloudFormation(Map cfg, Map envCfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |
| `envCfg` | `Map` | yes | — | One entry from `cfg.environments`. |

### Config keys read

| Key | Default | Sample value | Used by |
|---|---|---|---|
| `infra.template` | `template.yaml` | `templates/root.yaml` | [cfnChangeSet](cfnChangeSet.md) |
| `infra.capabilities` | `[CAPABILITY_IAM, CAPABILITY_NAMED_IAM]` | `[CAPABILITY_IAM]` | [cfnChangeSet](cfnChangeSet.md) |
| `infra.region` | `us-east-1` | `us-east-1` | [withAwsCredentials](withAwsCredentials.md) |
| `environments[].stackName` | `<appName>-<name>` | `billing-prod` | change set, execute |
| `environments[].parameters` | `[:]` | `{Environment: prod}` | [cfnChangeSet](cfnChangeSet.md) |
| `environments[].assumeRole` | `null` | `arn:aws:iam::444455556666:role/JenkinsDeploy` | [withAwsCredentials](withAwsCredentials.md) |
| `environments[].requiresApproval` | `false` | `true` | gate |
| `environments[].approvers` | `[]` | `[platform-leads, finance-eng]` | gate |

## Returns

Nothing. Logs `No changes for prod — nothing to execute` and returns when the
stack already matches the template.

## Examples

```yaml
# .ci/config.yaml — from examples/cloudformation-stack
appName: billing-stack
buildTool: cloudformation
infra:
  template: templates/root.yaml
  region: us-east-1
environments:
  - name: dev
    stackName: billing-dev
    parameters: {Environment: dev, InstanceType: t3.small}
    branchPattern: "*"
  - name: prod
    stackName: billing-prod
    parameters: {Environment: prod, InstanceType: m6i.large}
    assumeRole: arn:aws:iam::444455556666:role/JenkinsDeploy
    requiresApproval: true
    approvers: [platform-leads, finance-eng]
```

```groovy
deployCloudFormation(cfg, cfg.environments.find { it.name == 'prod' })
```

What happens:

| Step | Detail |
|---|---|
| 1. [cfnChangeSet](cfnChangeSet.md) | creates `billing-prod-42-ab12cd3`, summary `2 to add, 1 to modify` |
| 2. no changes? | stop here |
| 3. [postPlanSummary](postPlanSummary.md) | PR comment: `**prod**: 2 to add, 1 to modify` |
| 4. [approvalGate](../deploy/approvalGate.md) | Jenkins input *Deploy billing-stack 1.4.0 to prod?* for `platform-leads,finance-eng` |
| 5. [cfnDeploy](cfnDeploy.md) | executes that same change set and waits |

## How it fits

Called by [deployToEnvironment](../deploy/deployToEnvironment.md) when
`cfg.deployStrategy == 'cloudformation'`.

## Source

[`vars/deployCloudFormation.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/deployCloudFormation.groovy)
