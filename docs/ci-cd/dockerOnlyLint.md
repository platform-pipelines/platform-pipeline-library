# dockerOnlyLint

Nothing to compile for a `docker-only` repo, but the Dockerfile itself is
worth linting.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; `cfg.dockerfile` and `cfg.lint.failOnError` are read. |

## Returns

Nothing — errors if `hadolint` reports problems and `failOnError` is true.

## Usage

```groovy
dockerOnlyLint(cfg)
```

Called by [lintApp](lintApp.md) when `cfg.buildTool == 'docker-only'`.

## Source

[`vars/dockerOnlyLint.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/dockerOnlyLint.groovy)
