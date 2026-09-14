# gradleTest

Runs a Gradle project's test suite with JaCoCo coverage.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config (unused; kept for dispatcher parity). |

## Returns

Nothing — runs `gradle test jacocoTestReport`.

## Usage

```groovy
gradleTest(cfg)
```

Called by [testApp](testApp.md) when `cfg.buildTool == 'gradle'`.

## Source

[`vars/gradleTest.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/gradleTest.groovy)
