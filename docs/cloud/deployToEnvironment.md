# deployToEnvironment

Deploys to one environment, routing on `deployStrategy` — because deploying
an application and deploying infrastructure share an approval model but
nothing else.

## Signature

```groovy
def call(Map cfg, Map envCfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; `cfg.deployStrategy` picks the route. |
| `envCfg` | `Map` | Target environment config. |

## Returns

Nothing — delegates to [deployGitops](deployGitops.md),
[deployTerraform](deployTerraform.md), or
[deployCloudFormation](deployCloudFormation.md), and errors on an unknown
`deployStrategy`.

## Usage

```groovy
deployToEnvironment(cfg, envCfg)
```

For `gitops`, the [approvalGate](../ci-cd/approvalGate.md) runs *before* the
manifest bump — once it's committed, Argo acts on it, so the gate has to
come first. For `terraform`/`cloudformation`, the gate sits inside the
delegate, between plan and apply, so the approver sees the actual diff.

## Source

[`vars/deployToEnvironment.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/deployToEnvironment.groovy)
