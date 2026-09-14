# dockerOnlyTest

No-op test step for `docker-only` repos — there are no unit tests to run.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config (unused; kept for dispatcher parity). |

## Returns

Nothing — logs `docker-only: no unit tests`.

## Usage

```groovy
dockerOnlyTest(cfg)
```

Called by [testApp](testApp.md) when `cfg.buildTool == 'docker-only'`.

## Source

[`vars/dockerOnlyTest.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/dockerOnlyTest.groovy)
