# testApp

Routes to the right test step for this repo's `buildTool`.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.buildTool` to pick the test step. |

## Returns

Nothing. Delegates to the matching `*Test` step, or throws on an unsupported
`buildTool`.

## Usage

```groovy
testApp(cfg)
```

Dispatches to [goTest](goTest.md), [pythonTest](pythonTest.md),
[mavenTest](mavenTest.md), [gradleTest](gradleTest.md),
[nodeTest](nodeTest.md), [terraformTest](../cloud/terraformTest.md),
[cfnTest](../cloud/cfnTest.md), or [dockerOnlyTest](dockerOnlyTest.md) by
`cfg.buildTool`. Called from the `Test` stage of
[standardPipeline](standardPipeline.md).

## Source

[`vars/testApp.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/testApp.groovy)
