# buildImageKanikoDocker

Runs Kaniko as a one-shot Docker container on the agent — the default image
builder.

!!! note "Honest tradeoff"
    Works with the plugin set already pinned, which is why it's the default,
    but it needs a docker socket on the agent, so the agent isn't truly
    unprivileged. Move to [buildImageKanikoK8s](buildImageKanikoK8s.md) if
    that matters for your fleet.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; passed through to [kanikoArgs](kanikoArgs.md). |

## Returns

Nothing — runs Kaniko in a one-shot Docker container to build and push the
image.

## Usage

```groovy
buildImageKanikoDocker(cfg)
```

Called by [buildImage](buildImage.md) when `cfg.imageBuilder ==
'kaniko-docker'` (the default).

## Source

[`vars/buildImageKanikoDocker.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/buildImageKanikoDocker.groovy)
