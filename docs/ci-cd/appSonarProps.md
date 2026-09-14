# appSonarProps

Language-specific flags for `sonar-scanner`.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; only `cfg.buildTool` is read. |

## Returns

A `Map` of `sonar-scanner` properties for `cfg.buildTool` (coverage report
paths, JaCoCo/binaries locations, etc.), or `[:]` if none apply.

## Usage

```groovy
def props = appSonarProps(cfg)
```

Merged into the flag list built by [sonarProperties](sonarProperties.md).

## Source

[`vars/appSonarProps.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/appSonarProps.groovy)
