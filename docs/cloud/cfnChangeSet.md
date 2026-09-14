# cfnChangeSet

Creates a change set — CloudFormation's equivalent of a Terraform plan.
Returns the change set name, which [cfnDeploy](cfnDeploy.md) then executes.
Same principle as the Terraform path ([terraformPlan](terraformPlan.md) /
[terraformApply](terraformApply.md)): what gets executed is the thing that
was reviewed, not a freshly computed diff.

!!! note "Hardened against shell injection"
    `stack`, `changeSet`, `template`, and the CloudFormation tags are all
    shell-quoted before reaching `sh`. Parameter keys and capability names are
    validated against `^[A-Za-z0-9_-]+$` before use, and stack parameters are
    written to a JSON file (`cfn-params.json`) passed via `--parameters
    file://...` rather than built into a shell string by hand.

## Syntax

```groovy
def changeSet = cfnChangeSet(Map cfg, Map envCfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |
| `envCfg` | `Map` | yes | — | One entry from `cfg.environments`. |

Also reads `env.BUILD_NUMBER`, `env.GIT_SHORT_SHA`, `env.APP_VERSION` and
`env.GIT_COMMIT`.

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `appName` | — | `billing-stack` | Stack name fallback. |
| `infra.template` | `template.yaml` | `templates/root.yaml` | Template to deploy; `packaged-template.yaml` wins if [cfnPackage](cfnPackage.md) produced one. |
| `infra.capabilities` | `[CAPABILITY_IAM, CAPABILITY_NAMED_IAM]` | `[CAPABILITY_IAM]` | Passed as `--capabilities`. |
| `environments[].name` | — | `prod` | Stack name fallback; log banner. |
| `environments[].stackName` | `<appName>-<name>` | `billing-prod` | Stack to create or update. |
| `environments[].parameters` | `[:]` | `{Environment: prod, InstanceType: m6i.large}` | Stack parameters. Values are sent as strings. |
| `region` / `assumeRole` / `awsCredentialsId` | see [withAwsCredentials](withAwsCredentials.md) | | AWS access. |

## Returns

| Outcome | Return value | `env.CFN_HAS_CHANGES` | `env.CFN_CHANGE_SUMMARY` |
|---|---|---|---|
| Changes found | change set name, e.g. `billing-prod-42-ab12cd3` | `true` | `2 to add, 1 to modify` |
| Stack already matches | `null` (the empty change set is deleted) | `false` | `no changes` |
| Change set failed for a real reason | fails the build: `Change set … failed: failed: <reason>` | — | `failed: …` |

When there are changes, `changeset.json` (the full `describe-change-set`
output) is archived.

## Examples

```yaml
# .ci/config.yaml — from examples/cloudformation-stack
appName: billing-stack
buildTool: cloudformation
infra:
  template: templates/root.yaml
  region: us-east-1
  capabilities: [CAPABILITY_IAM, CAPABILITY_NAMED_IAM]
environments:
  - name: prod
    stackName: billing-prod
    parameters:
      Environment: prod
      InstanceType: m6i.large
    assumeRole: arn:aws:iam::444455556666:role/JenkinsDeploy
```

```groovy
def prod = cfg.environments.find { it.name == 'prod' }
def changeSet = cfnChangeSet(cfg, prod)      // → 'billing-prod-42-ab12cd3'
echo env.CFN_CHANGE_SUMMARY                  // → '1 to add, 2 to modify | REPLACEMENT: DatabaseInstance'
```

Files and commands it produces (`cfn-params.json` first):

```json
[
  {"ParameterKey": "Environment",  "ParameterValue": "prod"},
  {"ParameterKey": "InstanceType", "ParameterValue": "m6i.large"}
]
```

```bash
aws cloudformation create-change-set \
  --stack-name 'billing-prod' \
  --change-set-name 'billing-prod-42-ab12cd3' \
  --change-set-type UPDATE \
  --template-body 'file://templates/root.yaml' \
  --parameters file://cfn-params.json --capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM \
  --tags Key=Version,Value='1.4.0' Key=Commit,Value='ab12cd3ef4567890ab12cd3ef4567890ab12cd3e'
```

`--change-set-type` is `CREATE` the first time, when the stack doesn't exist.

Summary line formats:

| Summary | Meaning |
|---|---|
| `2 to add, 1 to modify` | normal change |
| `1 to modify, 1 to replace \| REPLACEMENT: DatabaseInstance` | a resource will be destroyed and recreated |
| `no changes` | stack already matches |

## How it fits

Called by [deployCloudFormation](deployCloudFormation.md), which passes the
result to [cfnDeploy](cfnDeploy.md) after an optional
[approvalGate](../ci-cd/approvalGate.md). Summarised by the bundled
`cfn_changeset_summary.py` (see [useScript](../other/useScript.md)).

## Source

[`vars/cfnChangeSet.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/cfnChangeSet.groovy)
