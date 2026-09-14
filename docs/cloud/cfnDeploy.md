# cfnDeploy

Executes a previously created change set and waits for the stack to settle.
On failure, pulls the last 25 stack events filtered to `CREATE_FAILED`/
`UPDATE_FAILED` so the cause shows up in the build log instead of requiring
someone to open the console.

## Signature

```groovy
def call(Map cfg, Map envCfg, String changeSet)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.appName`. |
| `envCfg` | `Map` | Target environment config; reads `envCfg.name`/`stackName`. |
| `changeSet` | `String` | Change set name from [cfnChangeSet](cfnChangeSet.md); a falsy value is a no-op. |

## Returns

Nothing — errors if the stack does not reach a complete state.

## Usage

```groovy
cfnDeploy(cfg, envCfg, changeSet)
```

Called by [deployCloudFormation](deployCloudFormation.md) as the last step,
after the change set has been reviewed (and approved, if
`envCfg.requiresApproval` is set).

## Source

[`vars/cfnDeploy.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/cfnDeploy.groovy)
