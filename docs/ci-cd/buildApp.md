# buildApp

Routes to the right build step for this repo's `buildTool`.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; `cfg.buildTool` selects the build step. |

## Returns

Nothing. Throws if `buildTool` is unsupported.

## Usage

```groovy
buildApp(cfg)
```

Dispatches to [goBuild](goBuild.md), [pythonBuild](pythonBuild.md),
[mavenBuild](mavenBuild.md), [gradleBuild](gradleBuild.md),
[nodeBuild](nodeBuild.md), [terraformBuild](../cloud/terraformBuild.md),
[cfnBuild](../cloud/cfnBuild.md), or [dockerOnlyBuild](dockerOnlyBuild.md) by
`cfg.buildTool`. Called from the `Build` stage of
[standardPipeline](standardPipeline.md).

## Source

[`vars/buildApp.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/buildApp.groovy)
