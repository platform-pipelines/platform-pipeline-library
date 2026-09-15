# configDeployStrategies

Single source of truth for legal `deployStrategy` values.

## Syntax

```groovy
configDeployStrategies()
```

## Parameters

None.

## Returns

`List<String>` — `['gitops', 'terraform', 'cloudformation']`.

| Value | Deploys by | Delegate |
|---|---|---|
| `gitops` | committing a new image tag to the GitOps repo | [deployGitops](../deploy/deployGitops.md) |
| `terraform` | plan → approve → apply the saved plan | [deployTerraform](../infrastructure/deployTerraform.md) |
| `cloudformation` | change set → approve → execute | [deployCloudFormation](../infrastructure/deployCloudFormation.md) |

## Examples

```groovy
configDeployStrategies()                      // → ['gitops', 'terraform', 'cloudformation']
'gitops' in configDeployStrategies()          // → true
'helm' in configDeployStrategies()            // → false
```

In `.ci/config.yaml` you rarely set it — it is derived from `buildTool`:

```yaml
buildTool: python           # deployStrategy defaults to gitops
# buildTool: terraform      # deployStrategy defaults to terraform
```

An unsupported value fails validation with:

```
deployStrategy 'helm' unsupported (use: gitops, terraform, cloudformation)
```

## How it fits

Read by [`configValidate`](configValidate.md) and by
[deployToEnvironment](../deploy/deployToEnvironment.md)'s error message. Adding
a strategy means adding it here and adding a case to `deployToEnvironment`.

## Source

[`vars/configDeployStrategies.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configDeployStrategies.groovy)
