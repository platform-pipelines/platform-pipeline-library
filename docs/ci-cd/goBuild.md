# goBuild

Builds a static Go binary, with the version stamped into `main.version`.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config (unused; kept for dispatcher parity with the other `*Build` steps). |

## Returns

Nothing — downloads modules and builds `./...`.

## Usage

```groovy
goBuild(cfg)
```

Called by [buildApp](buildApp.md) when `cfg.buildTool == 'go'`.

## Source

[`vars/goBuild.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/goBuild.groovy)
