# imageLabels

Computes the standard OCI labels so a running container can be traced back
to the commit and build that produced it.

## Syntax

```groovy
imageLabels(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; `cfg.appName` is read. |

Also reads `env.GIT_COMMIT`, `env.APP_VERSION`, `env.BUILD_URL` and
[githubRepoSlug](../other/githubRepoSlug.md).

## Returns

A `Map` of label → value:

| Label | Sample value |
|---|---|
| `org.opencontainers.image.source` | `https://github.com/acme/orders-api` |
| `org.opencontainers.image.revision` | `ab12cd3ef4567890ab12cd3ef4567890ab12cd3e` |
| `org.opencontainers.image.version` | `1.4.0` |
| `org.opencontainers.image.title` | `orders-api` |
| `ci.build.url` | `https://jenkins.acme.internal/job/orders-api/job/main/42/` (or `''`) |

Callers drop empty values before passing them to the builder.

## Examples

```groovy
def labels = imageLabels(cfg)
labels['org.opencontainers.image.version']     // → '1.4.0'
```

Reading them from a running image:

```bash
docker inspect ghcr.io/acme/orders-api:1.4.0 \
  --format '{{ index .Config.Labels "org.opencontainers.image.revision" }}'
```

## How it fits

Used by [buildImageBuildah](buildImageBuildah.md) and
[kanikoArgs](kanikoArgs.md).

## Source

[`vars/imageLabels.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/imageLabels.groovy)
