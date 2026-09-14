# dockerOnlyBuild

No-op build step for `docker-only` repos — there's nothing to compile before
the image build.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config (unused; kept for dispatcher parity). |

## Returns

Nothing — logs `docker-only: nothing to compile`.

## Usage

```groovy
dockerOnlyBuild(cfg)
```

Called by [buildApp](buildApp.md) when `cfg.buildTool == 'docker-only'`.

## Source

[`vars/dockerOnlyBuild.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/dockerOnlyBuild.groovy)
