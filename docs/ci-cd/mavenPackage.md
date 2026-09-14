# mavenPackage

Packages a Maven project into its build artifact, stamped with the resolved
version.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config (unused directly; kept for dispatch signature parity). |

## Returns

Nothing — runs `mvn package -DskipTests -Drevision=${env.APP_VERSION}`.

## Usage

```groovy
mavenPackage(cfg)
```

Called by [packageApp](packageApp.md) when `cfg.buildTool == 'maven'`.

## Source

[`vars/mavenPackage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/mavenPackage.groovy)
