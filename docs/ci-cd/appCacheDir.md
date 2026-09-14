# appCacheDir

Directory worth persisting between builds, or `null`.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; only `cfg.buildTool` is read. |

## Returns

The cache directory path for `cfg.buildTool` (e.g. `.m2` for `maven`), or
`null` if none applies.

## Usage

```groovy
def dir = appCacheDir(cfg)
```

## Source

[`vars/appCacheDir.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/appCacheDir.groovy)
