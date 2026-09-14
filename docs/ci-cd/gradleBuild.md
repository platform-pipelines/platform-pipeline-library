# gradleBuild

Compiles a Gradle-built Java project.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config (unused; kept for dispatcher parity). |

## Returns

Nothing — runs `gradle classes`.

## Usage

```groovy
gradleBuild(cfg)
```

Called by [buildApp](buildApp.md) when `cfg.buildTool == 'gradle'`. Uses the
shared flags from [gradleOpts](gradleOpts.md).

## Source

[`vars/gradleBuild.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/gradleBuild.groovy)
