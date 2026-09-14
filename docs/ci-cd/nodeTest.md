# nodeTest

Runs the project's `npm test` script.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config (unused directly; kept for dispatch signature parity). |

## Returns

Nothing — runs `npm test`.

## Usage

```groovy
nodeTest(cfg)
```

Called by [testApp](testApp.md) when `cfg.buildTool == 'npm'`.

## Source

[`vars/nodeTest.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/nodeTest.groovy)
