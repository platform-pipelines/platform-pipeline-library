# mavenTest

Runs `mvn verify` (not `test`) so Surefire and the JaCoCo report goal both
run — the coverage gate and Sonar scan both depend on the JaCoCo output.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config (unused directly; kept for dispatch signature parity). |

## Returns

Nothing — runs `mvn verify -DskipITs`.

## Usage

```groovy
mavenTest(cfg)
```

Called by [testApp](testApp.md) when `cfg.buildTool == 'maven'`.

## Source

[`vars/mavenTest.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/mavenTest.groovy)
