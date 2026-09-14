# pythonLint

Ruff covers what flake8, isort, and pyupgrade used to, in one fast pass. Runs
mypy too, if the project has a `mypy.ini` or `pyproject.toml`.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.lint.autoFormat` and `cfg.lint.failOnError`. |

## Returns

Nothing — writes `ruff-report.xml`, and errors if `ruff check`/`ruff format
--check` report problems and `failOnError` is true. mypy failures are
reported but never fail the build.

## Usage

```groovy
pythonLint(cfg)
```

Called by [lintApp](lintApp.md) when `cfg.buildTool == 'python'`.

## Source

[`vars/pythonLint.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/pythonLint.groovy)
