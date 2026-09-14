# buildImageKanikoDocker

Runs Kaniko as a one-shot Docker container on the agent — the default image
builder.

!!! note "Honest tradeoff"
    Works with the plugin set already pinned, which is why it's the default,
    but it needs a docker socket on the agent, so the agent isn't truly
    unprivileged. Move to [buildImageKanikoK8s](buildImageKanikoK8s.md) if
    that matters for your fleet.

## Syntax

```groovy
buildImageKanikoDocker(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; passed to [kanikoArgs](kanikoArgs.md). |

### Config keys read (via kanikoArgs)

| Key | Default | Sample value |
|---|---|---|
| `imageRepo` | — | `ghcr.io/acme/checkout-api` |
| `dockerfile` | `Dockerfile` | `Dockerfile` |
| `appName` | — | `checkout-api` |

## Returns

Nothing. Builds and pushes the image; Kaniko writes `image-digest.txt`.

## Examples

```yaml
# .ci/config.yaml — from examples/node-service
imageRepo: ghcr.io/acme/checkout-api
imageBuilder: kaniko-docker     # the default; can be omitted
```

```groovy
buildImageKanikoDocker(cfg)
```

Runs, inside `gcr.io/kaniko-project/executor:v1.23.2-debug` with the
workspace mounted at `/workspace`:

```bash
/kaniko/executor --context=dir:///workspace \
  --dockerfile=Dockerfile \
  --destination=ghcr.io/acme/checkout-api:1.4.0 \
  --destination=ghcr.io/acme/checkout-api:ab12cd3 \
  --destination=ghcr.io/acme/checkout-api:latest \
  --label org.opencontainers.image.title='checkout-api' ... \
  --build-arg APP_VERSION=1.4.0 \
  --cache=true --cache-repo=ghcr.io/acme/checkout-api/cache \
  --snapshot-mode=redo --digest-file=image-digest.txt
```

## How it fits

Called by [buildImage](buildImage.md) when `cfg.imageBuilder == 'kaniko-docker'`
(the default), after [kanikoDockerConfig](kanikoDockerConfig.md).

## Source

[`vars/buildImageKanikoDocker.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/buildImageKanikoDocker.groovy)
