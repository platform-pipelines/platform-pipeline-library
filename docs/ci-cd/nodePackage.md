# nodePackage

Stamps `package.json` with the resolved version and produces an `npm pack`
tarball.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config (unused directly; kept for dispatch signature parity). |

## Returns

Nothing — runs `npm version <APP_VERSION> --no-git-tag-version` then `npm pack`.

## Usage

```groovy
nodePackage(cfg)
```

Called by [packageApp](packageApp.md) when `cfg.buildTool == 'npm'`.

## Source

[`vars/nodePackage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/nodePackage.groovy)
