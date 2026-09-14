# inBuildContainer

Runs a build body in the right place for this agent: directly, on a toolbox
agent, or inside a per-language container otherwise.

## Signature

```groovy
def call(Map cfg, Closure body)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; determines the per-language image/cache dir. |
| `body` | `Closure` | Build steps to run. |

## Returns

Nothing — runs `body()` in place (toolbox agent) or inside the right
container via [inContainer](inContainer.md).

## Usage

```groovy
inBuildContainer(cfg) { sh 'go build ./...' }
```

Checks [usingToolbox](usingToolbox.md) first; otherwise resolves the image
and cache dir via [appToolImage](appToolImage.md) and
[appCacheDir](appCacheDir.md).

## Source

[`vars/inBuildContainer.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/inBuildContainer.groovy)
