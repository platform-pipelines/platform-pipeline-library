# sonarWaitForGate

Polls SonarQube for the quality gate result.

Uses the API rather than the plugin's `waitForQualityGate()`, which needs a
webhook back into Jenkins — awkward when Jenkins isn't publicly reachable.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.quality.sonarProjectKey` for the error/dashboard link. |

## Returns

Nothing. Polls for up to 10 minutes and throws unless the gate result is
`OK`.

## Usage

```groovy
sonarWaitForGate(cfg)
```

Called from [scanSonar](scanSonar.md) when `cfg.quality.failOnQualityGate` is
`true`. The polling logic itself runs in
`resources/com/platformpipelines/scripts/sonar_gate.py` via [useScript](../other/useScript.md).

## Source

[`vars/sonarWaitForGate.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/sonarWaitForGate.groovy)
