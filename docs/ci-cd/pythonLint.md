# pythonLint

Ruff covers what flake8, isort, and pyupgrade used to, in one fast pass. Runs
mypy too, if the project has a `mypy.ini` or `pyproject.toml`.

## Syntax

```groovy
pythonLint(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `lint.autoFormat` | `false` | `true` | Runs `ruff format .` and `ruff check --fix .` first. |
| `lint.failOnError` | `true` | `false` | `false` = ruff problems are reported, not fatal. |

## Returns

Nothing. Writes `ruff-report.xml` (JUnit). Fails the build with
`ruff reported problems. Run: ruff check --fix . && ruff format .` when
`ruff check` or `ruff format --check` fails and `failOnError` is `true`.
mypy output is shown but **never** fails the build.

## Examples

```yaml
buildTool: python
lint:
  failOnError: true
  autoFormat: false
```

```groovy
pythonLint(cfg)
```

Runs:

```bash
ruff check --output-format junit --output-file ruff-report.xml . || true
ruff check .
ruff format --check .
mypy . || true            # only when mypy.ini or pyproject.toml exists
```

Sample failure:

```
app/orders.py:14:5: F841 Local variable `total` is assigned to but never used
Would reformat: app/api.py
ERROR: ruff reported problems. Run: ruff check --fix . && ruff format .
```

Configure ruff in `pyproject.toml`:

```toml
[tool.ruff]
line-length = 100
target-version = "py312"

[tool.ruff.lint]
select = ["E", "F", "I", "UP", "B"]
```

## How it fits

Called by [lintApp](lintApp.md) when `cfg.buildTool == 'python'`.
`ruff-report.xml` is archived by [archiveLintReports](archiveLintReports.md).

## Source

[`vars/pythonLint.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/pythonLint.groovy)
