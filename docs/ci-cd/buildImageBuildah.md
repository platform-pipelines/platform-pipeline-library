# buildImageBuildah

Builds and pushes the image with Buildah, for agents running the toolbox
image with Buildah installed.

!!! note "Not a drop-in for every fleet"
    Rootless Buildah still needs subuid/subgid ranges and fuse-overlayfs on
    the host, so it's opt-in per repo (`imageBuilder: buildah`) rather than
    a default.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.dockerfile` and `cfg.imageRepo`. |

## Returns

Nothing — builds and pushes the image plus every tag from
[imageExtraTags](imageExtraTags.md).

## Usage

```groovy
buildImageBuildah(cfg)
```

Called by [buildImage](buildImage.md) when `cfg.imageBuilder == 'buildah'`.
Labels come from [imageLabels](imageLabels.md).

## Source

[`vars/buildImageBuildah.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/buildImageBuildah.groovy)
