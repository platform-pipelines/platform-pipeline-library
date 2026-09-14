# gradlePackage

Assembles a Gradle project's build artifact, stamped with the resolved
version.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config (unused; kept for dispatcher parity). |

## Returns

Nothing — runs `gradle assemble` with `-Pversion=${env.APP_VERSION}`.

## Usage

```groovy
gradlePackage(cfg)
```

Called by [packageApp](packageApp.md) when `cfg.buildTool == 'gradle'`.

## Source

[`vars/gradlePackage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/gradlePackage.groovy)
