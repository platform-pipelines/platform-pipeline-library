# gradlePackage

Assembles a Gradle project's build artifact, stamped with the resolved
version.

## Syntax

```groovy
gradlePackage(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; unused, kept so every `*Package` step has the same signature. |

Reads `env.APP_VERSION`.

## Returns

Nothing. Jars land in `build/libs/`, which [appArtifacts](../pipelines/appArtifacts.md)
(`build/libs/*.jar`) archives and [publishArtifact](../deploy/publishArtifact.md)
uploads.

## Examples

```groovy
gradlePackage(cfg)
```

Runs (with `APP_VERSION=1.4.0`):

```bash
gradle --no-daemon --console=plain assemble -Pversion=1.4.0
```

`-Pversion` sets the project version, so with `rootProject.name = "catalog-service"`
the output is `build/libs/catalog-service-1.4.0.jar`.

## How it fits

Called by [packageApp](../pipelines/packageApp.md) when `cfg.buildTool == 'gradle'`.

## Source

[`vars/gradlePackage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/gradlePackage.groovy)
