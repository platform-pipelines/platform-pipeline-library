# buildImageBuildah

Builds and pushes the image with Buildah, for agents running the toolbox
image with Buildah installed.

!!! note "Not a drop-in for every fleet"
    Rootless Buildah still needs subuid/subgid ranges and fuse-overlayfs on
    the host, so it's opt-in per repo (`imageBuilder: buildah`) rather than
    a default.

## Syntax

```groovy
buildImageBuildah(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

Reads `env.IMAGE_TAG`, `env.APP_VERSION`, `env.GIT_SHORT_SHA`, `env.BRANCH_NAME`.

### Config keys read

| Key | Default | Sample value |
|---|---|---|
| `imageRepo` | — | `ghcr.io/acme/orders-api` |
| `dockerfile` | `Dockerfile` | `Dockerfile` |
| `appName` | — | `orders-api` (OCI title label) |

## Returns

Nothing. Builds once and pushes the version tag plus every tag from
[imageExtraTags](imageExtraTags.md). Writes the pushed digest to
`image-digest.txt`.

## Examples

```yaml
# .ci/config.yaml — from examples/python-service
imageRepo: ghcr.io/acme/orders-api
imageBuilder: buildah
```

```groovy
buildImageBuildah(cfg)
```

Runs (on `main`, `IMAGE_TAG=1.4.0`):

```bash
buildah bud \
  --file Dockerfile \
  --build-arg APP_VERSION=1.4.0 \
  --label org.opencontainers.image.source='https://github.com/acme/orders-api' \
  --label org.opencontainers.image.revision='ab12cd3ef4567890ab12cd3ef4567890ab12cd3e' \
  --label org.opencontainers.image.version='1.4.0' \
  --label org.opencontainers.image.title='orders-api' \
  --tag ghcr.io/acme/orders-api:1.4.0 \
  .
buildah push --digestfile image-digest.txt ghcr.io/acme/orders-api:1.4.0 docker://ghcr.io/acme/orders-api:1.4.0
buildah push --digestfile image-digest.txt ghcr.io/acme/orders-api:1.4.0 docker://ghcr.io/acme/orders-api:ab12cd3
buildah push --digestfile image-digest.txt ghcr.io/acme/orders-api:1.4.0 docker://ghcr.io/acme/orders-api:latest
```

`APP_VERSION` is available as a build arg in the Dockerfile:

```dockerfile
ARG APP_VERSION=dev
ENV APP_VERSION=${APP_VERSION}
```

## How it fits

Called by [buildImage](buildImage.md) when `cfg.imageBuilder == 'buildah'`,
after [kanikoDockerConfig](kanikoDockerConfig.md). Labels come from
[imageLabels](imageLabels.md).

## Source

[`vars/buildImageBuildah.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/buildImageBuildah.groovy)
