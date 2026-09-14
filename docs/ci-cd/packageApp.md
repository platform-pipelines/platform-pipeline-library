# packageApp

Routes to the right package step for this repo's `buildTool`.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.buildTool`. |

## Returns

Nothing — dispatches to the matching `*Package` step.

## Usage

```groovy
packageApp(cfg)
```

Dispatches to [goPackage](goPackage.md), [pythonPackage](pythonPackage.md),
[mavenPackage](mavenPackage.md), [gradlePackage](gradlePackage.md),
[nodePackage](nodePackage.md), [terraformPackage](../cloud/terraformPackage.md),
[cfnPackage](../cloud/cfnPackage.md), or [dockerOnlyPackage](dockerOnlyPackage.md)
by `cfg.buildTool`. Called from the `Package` stage of
[standardPipeline](standardPipeline.md).

## Source

[`vars/packageApp.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/packageApp.groovy)
