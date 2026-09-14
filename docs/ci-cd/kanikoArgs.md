# kanikoArgs

Builds the Kaniko executor CLI flags shared by both Kaniko implementations.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.imageRepo` and `cfg.dockerfile`. |

## Returns

A `String` of Kaniko executor CLI flags — destinations (version tag plus
[imageExtraTags](imageExtraTags.md)), labels from
[imageLabels](imageLabels.md), build args, layer caching, and the digest
file path.

## Usage

```groovy
sh "/kaniko/executor ${kanikoArgs(cfg)}"
```

Used by [buildImageKanikoDocker](buildImageKanikoDocker.md) and
[buildImageKanikoK8s](buildImageKanikoK8s.md).

## Source

[`vars/kanikoArgs.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/kanikoArgs.groovy)
