# buildImage

Builds and pushes the container image. Routes to one of three
implementations because how you build an image without a root daemon depends
entirely on where your agents run.

## Syntax

```groovy
buildImage(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

Reads `env.IMAGE_TAG` (set by [initPipeline](initPipeline.md)).

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `imageRepo` | — (required) | `ghcr.io/acme/orders-api` | Where the image is pushed. |
| `dockerfile` | `Dockerfile` | `docker/Dockerfile.prod` | Must exist, or the build fails. |
| `imageBuilder` | `kaniko-docker` | `kaniko-k8s`, `buildah` | Which implementation runs. |

Registry credential: `REGISTRY_CREDENTIALS_ID` env var, default
`ghcr-credentials` (username/password).

## Returns

Nothing. Sets:

| Variable | Sample value |
|---|---|
| `env.IMAGE_REF` | `ghcr.io/acme/orders-api:1.4.0` |
| `env.IMAGE_DIGEST` | `sha256:7d9c…e41a` (empty if the builder wrote no digest) |

Fails the build with `Dockerfile not found at docker/Dockerfile.prod` or
`Unknown imageBuilder 'docker'. Use: kaniko-docker, kaniko-k8s, buildah`.

| `imageBuilder` | Implementation | Needs |
|---|---|---|
| `kaniko-docker` (default) | [buildImageKanikoDocker](buildImageKanikoDocker.md) | docker socket on the agent |
| `kaniko-k8s` | [buildImageKanikoK8s](buildImageKanikoK8s.md) | Kubernetes pod with a `kaniko` container |
| `buildah` | [buildImageBuildah](buildImageBuildah.md) | toolbox agent with rootless buildah set up |

## Examples

```yaml
# .ci/config.yaml
appName: orders-api
imageRepo: ghcr.io/acme/orders-api
dockerfile: Dockerfile
imageBuilder: kaniko-docker
```

```groovy
buildImage(cfg)
echo "pushed ${env.IMAGE_REF}@${env.IMAGE_DIGEST}"
```

On `main` with `IMAGE_TAG=1.4.0` and `GIT_SHORT_SHA=ab12cd3`, the image is
pushed as:

```
ghcr.io/acme/orders-api:1.4.0
ghcr.io/acme/orders-api:ab12cd3
ghcr.io/acme/orders-api:latest
```

Log and audit output:

```
====================================================================
  Build image ghcr.io/acme/orders-api:1.4.0
====================================================================
[INFO]  Pushed ghcr.io/acme/orders-api:1.4.0 (sha256:7d9c…e41a)
[AUDIT] image.push [image:ghcr.io/acme/orders-api:1.4.0, digest:sha256:7d9c…e41a, builder:kaniko-docker]
```

Pushing to a different registry:

```groovy
withEnv(['REGISTRY_CREDENTIALS_ID=harbor-robot']) {
    buildImage(cfg + [imageRepo: 'harbor.acme.internal/orders/orders-api'])
}
```

## How it fits

Calls [kanikoDockerConfig](kanikoDockerConfig.md) first to write registry
auth, then dispatches by `imageBuilder`. Called from
[standardPipeline](standardPipeline.md)'s `Package` stage when
`containerize: true`, before [generateSbom](generateSbom.md) and
[signImage](signImage.md).

## Source

[`vars/buildImage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/buildImage.groovy)
