# publishArtifact

Uploads build output to Nexus over REST, so the same command works from a
laptop as from an agent.

## Syntax

```groovy
publishArtifact(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

Reads `env.NEXUS_URL` and `env.APP_VERSION`.

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `publish.nexusRepo` | `null` | `pypi-internal` | Nexus repository name. Unset → nothing is published. |
| `appName` | — | `orders-api` | First path segment of the upload. |
| `buildTool` | — | `python` | Which files to upload, via [appArtifacts](appArtifacts.md). |

The old `extra.nexusRepo` still works, with a deprecation warning.

### Controller requirements

| Item | Kind | Sample value |
|---|---|---|
| `NEXUS_URL` | environment variable | `https://nexus.acme.internal` |
| `nexus-credentials` | username/password credential | a Nexus user with write access to the repo |

## Returns

Nothing. Uploads every file matching the artifact glob. Skips (with a debug
or warning line) when `publish.nexusRepo` is unset, the tool has no artifact
glob, or no files match. Fails the build if an upload fails.

## Examples

```yaml
# .ci/config.yaml — from examples/python-service
appName: orders-api
buildTool: python
publish:
  nexusRepo: pypi-internal
```

```groovy
packageApp(cfg)          // writes dist/orders_api-1.4.0-py3-none-any.whl, dist/orders_api-1.4.0.tar.gz
publishArtifact(cfg)
```

Uploads (one `curl --upload-file` per file):

```
https://nexus.acme.internal/repository/pypi-internal/orders-api/1.4.0/orders_api-1.4.0-py3-none-any.whl
https://nexus.acme.internal/repository/pypi-internal/orders-api/1.4.0/orders_api-1.4.0.tar.gz
```

Log and audit output:

```
====================================================================
  Publish to Nexus (pypi-internal)
====================================================================
[INFO]  Published orders_api-1.4.0-py3-none-any.whl
[INFO]  Published orders_api-1.4.0.tar.gz
[AUDIT] artifact.publish [repository:pypi-internal, version:1.4.0, count:2]
```

!!! note "Raw upload path"
    Files are PUT to `<repo>/<appName>/<version>/<file>`. That suits a Nexus
    *raw* (hosted) repository; format-specific repositories (maven2, pypi,
    npm) expect their own layouts.

## How it fits

Called from the `Package` stage of [standardPipeline](standardPipeline.md)
for non-infra repos, after [packageApp](packageApp.md).

## Source

[`vars/publishArtifact.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/publishArtifact.groovy)
