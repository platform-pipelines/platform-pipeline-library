# mavenPackage

Packages a Maven project into its build artifact, stamped with the resolved
version.

## Syntax

```groovy
mavenPackage(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; unused, kept so every `*Package` step has the same signature. |

Reads `env.APP_VERSION`.

## Returns

Nothing. Jars land in `target/`, which [appArtifacts](appArtifacts.md)
(`target/*.jar`) archives and [publishArtifact](publishArtifact.md) uploads.

## Examples

```groovy
mavenPackage(cfg)
```

Runs (with `APP_VERSION=1.4.0`):

```bash
mvn -B -ntp -Dmaven.repo.local=.m2 package -DskipTests -Drevision=1.4.0
```

Tests are skipped because [mavenTest](mavenTest.md) already ran them.
`-Drevision` only takes effect if the POM uses the CI-friendly version
property:

```xml
<version>${revision}</version>
<properties>
  <revision>0.0.0-SNAPSHOT</revision>
</properties>
```

## How it fits

Called by [packageApp](packageApp.md) when `cfg.buildTool == 'maven'`.

## Source

[`vars/mavenPackage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/mavenPackage.groovy)
