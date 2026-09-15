# gradleLint

Runs Checkstyle and SpotBugs against a Gradle project.

## Syntax

```groovy
gradleLint(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `lint.autoFormat` | `false` | `true` | Runs `spotlessApply` first (its failure is ignored). |
| `lint.failOnError` | `true` | `false` | `false` = violations are reported, not fatal. |

## Returns

Nothing. Fails the build with `Checkstyle or SpotBugs reported violations`
when either task fails and `failOnError` is `true`.

## Examples

```yaml
buildTool: gradle
lint:
  failOnError: true
  autoFormat: false
```

```groovy
gradleLint(cfg)
```

Runs:

```bash
gradle --no-daemon --console=plain checkstyleMain spotbugsMain
# with lint.autoFormat: true, first:
gradle --no-daemon --console=plain spotlessApply || true
```

The project must apply the plugins that provide those tasks:

```kotlin
// build.gradle.kts
plugins {
    java
    checkstyle
    id("com.github.spotbugs") version "6.1.7"
    id("com.diffplug.spotless") version "7.0.2"   // only needed for autoFormat
}
```

## How it fits

Called by [lintApp](../pipelines/lintApp.md) when `cfg.buildTool == 'gradle'`. Reports
under `build/reports/checkstyle/` are archived by
[archiveLintReports](../quality/archiveLintReports.md). Flags come from
[gradleOpts](gradleOpts.md).

## Source

[`vars/gradleLint.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/gradleLint.groovy)
