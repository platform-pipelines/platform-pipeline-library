# mavenBuild

Compiles a Maven project.

## Syntax

```groovy
mavenBuild(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; unused, kept so every `*Build` step has the same signature. |

## Returns

Nothing. Fails the build on a compile error.

## Examples

```yaml
appName: catalog-service
buildTool: maven
runtimeVersion: "21"
```

```groovy
mavenBuild(cfg)
```

Runs:

```bash
mvn -B -ntp -Dmaven.repo.local=.m2 clean compile
```

Off the toolbox this runs in `maven:3.9-eclipse-temurin-21`.

## How it fits

Called by [buildApp](buildApp.md) when `cfg.buildTool == 'maven'`. Flags
come from [mavenOpts](mavenOpts.md).

## Source

[`vars/mavenBuild.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/mavenBuild.groovy)
