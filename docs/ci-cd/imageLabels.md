# imageLabels

Computes the standard OCI labels so a running container can be traced back
to the commit and build that produced it.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; `cfg.appName` is read. |

## Returns

`Map` of OCI label keys to values (`org.opencontainers.image.source`,
`.revision`, `.version`, `.title`, plus `ci.build.url`).

## Usage

```groovy
def labels = imageLabels(cfg)
```

Used by [buildImageBuildah](buildImageBuildah.md) and
[kanikoArgs](kanikoArgs.md) when building an image.

## Source

[`vars/imageLabels.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/imageLabels.groovy)
