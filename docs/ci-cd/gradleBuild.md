# gradleBuild

Compiles a Gradle-built Java project.

## Syntax

```groovy
gradleBuild(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; unused, kept so every `*Build` step has the same signature. |

## Returns

Nothing. Fails the build on a compile error.

## Examples

```yaml
# .ci/config.yaml — from examples/java-service
appName: catalog-service
buildTool: gradle
runtimeVersion: "21"
```

```groovy
gradleBuild(cfg)
```

Runs:

```bash
gradle --no-daemon --console=plain classes
```

Off the toolbox this runs in `gradle:9-jdk21`. The system `gradle` is used,
not `./gradlew`.

## How it fits

Called by [buildApp](buildApp.md) when `cfg.buildTool == 'gradle'`. Flags
come from [gradleOpts](gradleOpts.md).

## Source

[`vars/gradleBuild.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/gradleBuild.groovy)
