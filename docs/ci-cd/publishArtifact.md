# publishArtifact

Publishes build output to every artifact store the repo turned on: Nexus
and/or GitHub Packages in the repo's own namespace. Both can be on at once.

## Syntax

```groovy
publishArtifact(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `publish.nexusRepo` | `null` | `pypi-internal` | Upload to this Nexus repository with [publishArtifactNexus](publishArtifactNexus.md). |
| `publish.githubPackages` | `false` | `true` | Push to `ghcr.io/<owner>/<repo>/<appName>-artifacts:<tag>` with [publishArtifactGithub](publishArtifactGithub.md). |
| `appName` | — | `orders-api` | Names the upload path / package. |
| `buildTool` | — | `python` | Which files to publish, via [appArtifacts](appArtifacts.md). |

The old `extra.nexusRepo` still works, with a deprecation warning.

## Returns

Nothing. Skips (with a debug or warning line) when neither store is
configured, the tool has no artifact glob, or no files match. Fails the build
if an upload fails.

## Examples

```yaml
# .ci/config.yaml
appName: orders-api
buildTool: python
publish:
  nexusRepo: pypi-internal   # optional
  githubPackages: true       # optional
```

```groovy
packageApp(cfg)          // writes dist/orders_api-1.4.0-py3-none-any.whl, dist/orders_api-1.4.0.tar.gz
publishArtifact(cfg)
```

```
====================================================================
  Publish to Nexus (pypi-internal)
====================================================================
[INFO]  Published orders_api-1.4.0-py3-none-any.whl
[INFO]  Published orders_api-1.4.0.tar.gz
[AUDIT] artifact.publish [store:nexus, repository:pypi-internal, version:1.4.0, count:2]
====================================================================
  Publish to GitHub Packages (ghcr.io/acme/orders-api/orders-api-artifacts:1.4.0)
====================================================================
[INFO]  Published orders_api-1.4.0-py3-none-any.whl
[INFO]  Published orders_api-1.4.0.tar.gz
[AUDIT] artifact.publish [store:github, repository:ghcr.io/acme/orders-api/orders-api-artifacts:1.4.0, version:1.4.0, count:2]
```

## How it fits

Called from the `Package` stage of [standardPipeline](standardPipeline.md)
for non-infra repos, after [packageApp](packageApp.md) and before the image
build.

## Source

[`vars/publishArtifact.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/publishArtifact.groovy)
