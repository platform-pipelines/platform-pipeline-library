# dockerOnlyTest

No-op test step for `docker-only` repos — there are no unit tests to run.

## Syntax

```groovy
dockerOnlyTest(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; unused, kept so every `*Test` step has the same signature. |

## Returns

Nothing. Logs one line.

## Examples

```groovy
dockerOnlyTest(cfg)
```

Output:

```
[INFO]  docker-only: no unit tests
```

The built image is still scanned by [scanTrivy](../quality/scanTrivy.md) in the
`Scan Image` stage.

## How it fits

Called by [testApp](../pipelines/testApp.md) when `cfg.buildTool == 'docker-only'`.

## Source

[`vars/dockerOnlyTest.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/dockerOnlyTest.groovy)
