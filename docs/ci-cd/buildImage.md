# buildImage

Builds and pushes the container image. Routes to one of three
implementations because how you build an image without a root daemon depends
entirely on where your agents run.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.dockerfile`, `cfg.imageRepo`, `cfg.imageBuilder`. |

## Returns

Nothing — sets `env.IMAGE_DIGEST`/`env.IMAGE_REF`, and errors if the
Dockerfile is missing or `imageBuilder` is unrecognized.

## Usage

```groovy
buildImage(cfg)
```

Dispatches by `cfg.imageBuilder` to [buildImageKanikoK8s](buildImageKanikoK8s.md)
(`kaniko-k8s`), [buildImageKanikoDocker](buildImageKanikoDocker.md)
(`kaniko-docker`, the default), or [buildImageBuildah](buildImageBuildah.md)
(`buildah`). Calls [kanikoDockerConfig](kanikoDockerConfig.md) first to set
up registry auth.

## Source

[`vars/buildImage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/buildImage.groovy)
