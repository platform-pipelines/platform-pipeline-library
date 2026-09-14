# lintApp

Routes to the right lint step for this repo's `buildTool`.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.lint.enabled` and `cfg.buildTool`. |

## Returns

Nothing — dispatches to the matching `*Lint` step, or throws if
`cfg.buildTool` has no lint step.

## Usage

```groovy
lintApp(cfg)
```

Dispatches to one of [goLint](goLint.md), [pythonLint](pythonLint.md),
[mavenLint](mavenLint.md), [gradleLint](gradleLint.md),
[nodeLint](nodeLint.md), [terraformLint](../cloud/terraformLint.md) (in
[Cloud](../cloud/index.md)), [cfnLint](../cloud/cfnLint.md), or
[dockerOnlyLint](dockerOnlyLint.md), by `cfg.buildTool`. A no-op when
`cfg.lint.enabled` is `false`.

## Source

[`vars/lintApp.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/lintApp.groovy)
