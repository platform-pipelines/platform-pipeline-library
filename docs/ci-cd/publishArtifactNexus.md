# publishArtifactNexus

Uploads build output to Nexus over REST, so the same command works from a
laptop as from an agent.

## Syntax

```groovy
publishArtifactNexus(Map cfg, List files)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; reads `publish.nexusRepo` and `appName`. |
| `files` | `List` | yes | — | Files to upload; each needs `.path` and `.name`, as `findFiles` returns. |

Reads `env.NEXUS_URL` and `env.APP_VERSION`.

### Controller requirements

| Item | Kind | Sample value |
|---|---|---|
| `NEXUS_URL` | environment variable | `https://nexus.acme.internal` |
| `nexus-credentials` | username/password credential | a Nexus user with write access to the repo |

## Returns

Nothing. One `curl --upload-file` per file; fails the build if an upload fails.

## Examples

```groovy
publishArtifactNexus(cfg, findFiles(glob: 'dist/*') as List)
```

Uploads:

```
https://nexus.acme.internal/repository/pypi-internal/orders-api/1.4.0/orders_api-1.4.0-py3-none-any.whl
https://nexus.acme.internal/repository/pypi-internal/orders-api/1.4.0/orders_api-1.4.0.tar.gz
```

```
[AUDIT] artifact.publish [store:nexus, repository:pypi-internal, version:1.4.0, count:2]
```

!!! note "Raw upload path"
    Files are PUT to `<repo>/<appName>/<version>/<file>`. That suits a Nexus
    *raw* (hosted) repository; format-specific repositories (maven2, pypi,
    npm) expect their own layouts.

## How it fits

Called by [publishArtifact](publishArtifact.md) when `publish.nexusRepo` is set.

## Source

[`vars/publishArtifactNexus.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/publishArtifactNexus.groovy)
