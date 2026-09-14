# goPackage

Builds a stripped, statically-linked release binary for a Go service.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; `cfg.appName` is read. |

## Returns

Nothing — builds a stripped static binary into `dist/<appName>`, with
`main.version` stamped from `env.APP_VERSION`.

## Usage

```groovy
goPackage(cfg)
```

Called by [packageApp](packageApp.md) when `cfg.buildTool == 'go'`.

## Source

[`vars/goPackage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/goPackage.groovy)
