# dockerOnlyPackage

No-op package step for `docker-only` repos — the image itself is the
artifact.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config (unused; kept for dispatcher parity). |

## Returns

Nothing — logs `docker-only: the image is the artifact`.

## Usage

```groovy
dockerOnlyPackage(cfg)
```

Called by [packageApp](packageApp.md) when `cfg.buildTool == 'docker-only'`.

## Source

[`vars/dockerOnlyPackage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/dockerOnlyPackage.groovy)
