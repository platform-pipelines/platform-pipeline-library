# pythonVenv

Creates or reuses `.venv` and installs `requirements.txt` and
`requirements-dev.txt` into it. Installing into the system interpreter fails
both in `python:*-slim` (read-only site-packages for the agent uid) and in the
toolbox (PEP 668).

## Syntax

```groovy
String pythonVenv(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config (unused; signature parity). |

## Returns

`'.venv/bin'`.

## Examples

```groovy
def bin = pythonVenv(cfg)
sh "${bin}/python -m pytest"
```

| File present | Runs |
|---|---|
| no `.venv/bin/python` | `python3 -m venv .venv` |
| `requirements.txt` | `.venv/bin/python -m pip install … -r requirements.txt` |
| `requirements-dev.txt` | `.venv/bin/python -m pip install … -r requirements-dev.txt` |

Put pytest, pytest-cov, ruff and mypy in `requirements-dev.txt`, and `.venv` in
`.dockerignore`. Called by [pythonLint](pythonLint.md), [pythonBuild](pythonBuild.md),
[pythonTest](pythonTest.md) and [pythonPackage](pythonPackage.md).

## Source

[`vars/pythonVenv.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/pythonVenv.groovy)
