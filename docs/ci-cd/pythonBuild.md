# pythonBuild

Installs requirements and compiles all modules as a fast syntax check.

## Syntax

```groovy
pythonBuild(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; unused, kept so every `*Build` step has the same signature. |

## Returns

Nothing. Fails the build if `requirements.txt` is missing, an install fails,
or any file has a syntax error.

## Examples

```yaml
# .ci/config.yaml — from examples/python-service
appName: orders-api
buildTool: python
runtimeVersion: "3.12"
```

```groovy
pythonBuild(cfg)
```

Runs:

```bash
pip install --no-cache-dir -r requirements.txt
python -m compileall -q .
```

Off the toolbox this runs in `python:3.12-slim`.

!!! note "requirements.txt is required"
    Projects that keep dependencies only in `pyproject.toml` need a
    `requirements.txt` (for example `-e .` or an exported lock file) for
    this step.

## How it fits

Called by [buildApp](buildApp.md) when `cfg.buildTool == 'python'`.

## Source

[`vars/pythonBuild.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/pythonBuild.groovy)
