# scanSonar

Runs SonarQube analysis, then the quality gate if the config demands it.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.quality.failOnQualityGate`. |

## Returns

Nothing. Runs `sonar-scanner` and, if `cfg.quality.failOnQualityGate` is
`true`, blocks on [sonarWaitForGate](sonarWaitForGate.md).

## Usage

```groovy
scanSonar(cfg)
```

The `-D` flags passed to `sonar-scanner` come from
[sonarProperties](sonarProperties.md). Called from
[qualityChecks](qualityChecks.md) when `cfg.quality.sonar` is `true`.

## Source

[`vars/scanSonar.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/scanSonar.groovy)
