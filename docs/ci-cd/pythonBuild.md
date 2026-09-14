# pythonBuild

Installs requirements and compiles all modules as a fast syntax check.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config (unused directly; kept for dispatch signature parity). |

## Returns

Nothing — runs `pip install -r requirements.txt` then `python -m compileall -q .`.

## Usage

```groovy
pythonBuild(cfg)
```

Called by [buildApp](buildApp.md) when `cfg.buildTool == 'python'`.

## Source

[`vars/pythonBuild.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/pythonBuild.groovy)
