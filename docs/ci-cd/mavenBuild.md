# mavenBuild

Compiles a Maven project.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config (unused directly; kept for dispatch signature parity). |

## Returns

Nothing — runs `mvn clean compile`.

## Usage

```groovy
mavenBuild(cfg)
```

Called by [buildApp](buildApp.md) when `cfg.buildTool == 'maven'`. Uses the
shared flags from [mavenOpts](mavenOpts.md).

## Source

[`vars/mavenBuild.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/mavenBuild.groovy)
