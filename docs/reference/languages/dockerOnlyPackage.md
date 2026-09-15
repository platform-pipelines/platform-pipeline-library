# dockerOnlyPackage

No-op package step for `docker-only` repos — the image itself is the
artifact.

## Syntax

```groovy
dockerOnlyPackage(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; unused, kept so every `*Package` step has the same signature. |

## Returns

Nothing. Logs one line.

## Examples

```groovy
dockerOnlyPackage(cfg)
```

Output:

```
[INFO]  docker-only: the image is the artifact
```

The image is still built and pushed afterwards by [buildImage](../images/buildImage.md),
because `containerize` defaults to `true`.

## How it fits

Called by [packageApp](../pipelines/packageApp.md) when `cfg.buildTool == 'docker-only'`.

## Source

[`vars/dockerOnlyPackage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/dockerOnlyPackage.groovy)
