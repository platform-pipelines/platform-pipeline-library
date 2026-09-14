# pythonPackage

Builds the project's sdist/wheel via PEP 517's `build`.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config (unused directly; kept for dispatch signature parity). |

## Returns

Nothing — installs `build` and runs `python -m build`.

## Usage

```groovy
pythonPackage(cfg)
```

Called by [packageApp](packageApp.md) when `cfg.buildTool == 'python'`.

## Source

[`vars/pythonPackage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/pythonPackage.groovy)
