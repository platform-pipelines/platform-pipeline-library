# cfnDeploy

Executes a previously created change set and waits for the stack to settle.
On failure, pulls the last 25 stack events filtered to `CREATE_FAILED`/
`UPDATE_FAILED` so the cause shows up in the build log instead of requiring
someone to open the console.

## Syntax

```groovy
cfnDeploy(Map cfg, Map envCfg, String changeSet)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; reads `appName` (stack name fallback). |
| `envCfg` | `Map` | yes | — | Target environment; reads `name`, `stackName`, and AWS overrides. |
| `changeSet` | `String` | yes | — | Name returned by [cfnChangeSet](cfnChangeSet.md). `null`/empty = nothing to do. |

Also reads `env.CFN_CHANGE_SUMMARY` and `env.DEPLOY_APPROVER` for the audit record.

## Returns

Nothing. On success writes and archives `cfn-outputs.json` (the stack
outputs). Fails the build with `Stack billing-prod did not reach a complete state`
if the update or create doesn't complete. With no change set it logs
`No change set — stack already matches the template` and returns.

## Examples

```groovy
def prod = cfg.environments.find { it.name == 'prod' }
def changeSet = cfnChangeSet(cfg, prod)        // → 'billing-prod-42-ab12cd3'
cfnDeploy(cfg, prod, changeSet)
```

Runs:

```bash
aws cloudformation execute-change-set --stack-name 'billing-prod' --change-set-name 'billing-prod-42-ab12cd3'
aws cloudformation wait stack-update-complete --stack-name 'billing-prod' \
  || aws cloudformation wait stack-create-complete --stack-name 'billing-prod'
aws cloudformation describe-stacks --stack-name 'billing-prod' --query 'Stacks[0].Outputs' > cfn-outputs.json
```

Failure output:

```
-------------------------------------------------------------------------
|                          DescribeStackEvents                          |
+-------------------+---------------------------------------------------+
|  DatabaseInstance |  DB instance class m6i.large is not supported ... |
+-------------------+---------------------------------------------------+
ERROR: Stack billing-prod did not reach a complete state
```

Reading an output afterwards:

```groovy
def outputs = readJSON file: 'cfn-outputs.json'
def apiUrl = outputs.find { it.OutputKey == 'ApiUrl' }?.OutputValue
```

Audit record:

```
[AUDIT] infra.apply [environment:prod, stack:billing-prod, summary:2 to add, 1 to modify, approver:sam.lee]
```

## How it fits

Called by [deployCloudFormation](deployCloudFormation.md) as the last step,
after the change set has been reviewed (and approved, if
`requiresApproval` is set). Credentials come from
[withAwsCredentials](withAwsCredentials.md).

## Source

[`vars/cfnDeploy.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/cfnDeploy.groovy)
