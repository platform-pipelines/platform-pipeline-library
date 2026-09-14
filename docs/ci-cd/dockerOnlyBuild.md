# dockerOnlyBuild

No-op build step for `docker-only` repos — there's nothing to compile before
the image build.

## Syntax

```groovy
dockerOnlyBuild(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; unused, kept so every `*Build` step has the same signature. |

## Returns

Nothing. Logs one line.

## Examples

```yaml
appName: nginx-edge
buildTool: docker-only
imageRepo: ghcr.io/acme/nginx-edge
```

```groovy
dockerOnlyBuild(cfg)
```

Output:

```
[INFO]  docker-only: nothing to compile
```

Put any compile work in the Dockerfile instead (multi-stage build).

## How it fits

Called by [buildApp](buildApp.md) when `cfg.buildTool == 'docker-only'`.

## Source

[`vars/dockerOnlyBuild.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/dockerOnlyBuild.groovy)
