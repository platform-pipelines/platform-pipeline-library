# kanikoArgs

Builds the Kaniko executor CLI flags shared by both Kaniko implementations.

## Syntax

```groovy
kanikoArgs(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

Reads `env.IMAGE_TAG` and `env.APP_VERSION`.

### Config keys read

| Key | Default | Sample value |
|---|---|---|
| `imageRepo` | — | `ghcr.io/acme/orders-api` |
| `dockerfile` | `Dockerfile` | `Dockerfile` |
| `appName` | — | `orders-api` (via [imageLabels](imageLabels.md)) |

## Returns

A `String` of executor flags (without `--context`):

| Flag | Sample value |
|---|---|
| `--dockerfile` | `Dockerfile` |
| `--destination` (one per tag) | `ghcr.io/acme/orders-api:1.4.0`, `:ab12cd3`, `:latest` |
| `--label` (one per non-empty label) | `org.opencontainers.image.version='1.4.0'` |
| `--build-arg` | `APP_VERSION=1.4.0` |
| `--cache` / `--cache-repo` | `true` / `ghcr.io/acme/orders-api/cache` |
| `--snapshot-mode` | `redo` |
| `--digest-file` | `image-digest.txt` |

## Examples

```groovy
sh "/kaniko/executor --context=dir://\$(pwd) ${kanikoArgs(cfg)}"
```

Result on `main` for the sample config:

```
--dockerfile=Dockerfile \
--destination=ghcr.io/acme/orders-api:1.4.0 --destination=ghcr.io/acme/orders-api:ab12cd3 --destination=ghcr.io/acme/orders-api:latest \
--label org.opencontainers.image.source='https://github.com/acme/orders-api' --label org.opencontainers.image.revision='ab12cd3e…' --label org.opencontainers.image.version='1.4.0' --label org.opencontainers.image.title='orders-api' \
--build-arg APP_VERSION=1.4.0 \
--cache=true \
--cache-repo=ghcr.io/acme/orders-api/cache \
--snapshot-mode=redo \
--digest-file=image-digest.txt
```

!!! note "Cache repository"
    Layer cache is pushed to `<imageRepo>/cache`, so the registry credential
    needs push access there too.

## How it fits

Used by [buildImageKanikoDocker](buildImageKanikoDocker.md) and
[buildImageKanikoK8s](buildImageKanikoK8s.md).

## Source

[`vars/kanikoArgs.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/kanikoArgs.groovy)
