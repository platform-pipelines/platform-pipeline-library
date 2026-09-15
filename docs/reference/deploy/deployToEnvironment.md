# deployToEnvironment

Deploys to one environment, routing on `deployStrategy` — because deploying
an application and deploying infrastructure share an approval model but
nothing else.

## Syntax

```groovy
deployToEnvironment(Map cfg, Map envCfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; `cfg.deployStrategy` picks the route. |
| `envCfg` | `Map` | yes | — | One entry from `cfg.environments`. |

### Config keys read

| Key | Default | Sample value |
|---|---|---|
| `deployStrategy` | derived — `gitops` for apps, `buildTool` for infra | `gitops` |
| `environments[].name` | — | `prod` |
| `environments[].requiresApproval` | `false` | `true` |

## Returns

Nothing. Delegates as below, and fails the build on an unknown strategy:
`Unknown deployStrategy 'helm'. Use: gitops, terraform, cloudformation`.

| `deployStrategy` | Order of operations |
|---|---|
| `gitops` | [approvalGate](approvalGate.md) (if required) → [deployGitops](deployGitops.md) |
| `terraform` | [deployTerraform](../infrastructure/deployTerraform.md): plan → gate → apply |
| `cloudformation` | [deployCloudFormation](../infrastructure/deployCloudFormation.md): change set → gate → execute |

For `gitops`, the gate runs *before* the manifest bump — once it's committed,
Argo acts on it, so the gate has to come first. For `terraform`/`cloudformation`,
the gate sits inside the delegate, between plan and apply, so the approver
sees the actual diff.

## Examples

```groovy
deployToEnvironment(cfg, cfg.environments.find { it.name == 'prod' })
```

Deploying every environment this branch may reach, one after another (what
[standardPipeline](../pipelines/standardPipeline.md) does):

```groovy
configEnvironmentsFor(cfg, env.BRANCH_NAME).each { envCfg ->
    stage("Deploy: ${envCfg.name}") {
        node('linux') {
            deployToEnvironment(cfg, envCfg)
        }
    }
}
```

Log output begins with:

```
====================================================================
  Deploy -> prod
====================================================================
```

## Source

[`vars/deployToEnvironment.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/deployToEnvironment.groovy)
