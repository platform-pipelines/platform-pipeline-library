# inBuildContainer

Runs a build body in the right place for this agent: directly on a toolbox
agent, or inside a per-language container otherwise.

## Syntax

```groovy
inBuildContainer(Map cfg) {
    // build steps
}
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; `buildTool` and `runtimeVersion` pick the fallback image. |
| `body` | `Closure` | yes | — | Build steps to run. |

## Returns

Nothing (the value of `body` is not returned).

| Agent | Where `body` runs |
|---|---|
| `CI_TOOLBOX=true` ([usingToolbox](usingToolbox.md)) | directly on the agent |
| any other agent | inside [`appToolImage(cfg)`](../pipelines/appToolImage.md), with the [`appCacheDir(cfg)`](../pipelines/appCacheDir.md) volume, via [inContainer](inContainer.md) |

## Examples

```yaml
buildTool: python
runtimeVersion: "3.12"
```

```groovy
inBuildContainer(cfg) {
    sh 'pip install -r requirements.txt && pytest -q'
}
```

On a plain docker agent this is equivalent to:

```groovy
inContainer('python:3.12-slim', '.pip-cache') {
    sh 'pip install -r requirements.txt && pytest -q'
}
```

On a toolbox agent it just runs the `sh` step.

## How it fits

Wraps every build-type stage in [standardPipeline](../pipelines/standardPipeline.md)
(Lint, Build, Test, Package).

## Source

[`vars/inBuildContainer.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/inBuildContainer.groovy)
