# pythonTest

Runs the Python test suite with coverage and JUnit XML output.

## Syntax

```groovy
pythonTest(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; unused, kept so every `*Test` step has the same signature. |

## Returns

Nothing. Fails the build if any test fails. Produces:

| File | Used by |
|---|---|
| `test-results.xml` | JUnit results in Jenkins |
| `coverage.xml` | [checkCoverage](checkCoverage.md), Sonar |

## Examples

```yaml
buildTool: python
quality:
  minCoverage: 75
```

```groovy
pythonTest(cfg)
```

Runs:

```bash
pytest --junitxml=test-results.xml --cov=. --cov-report=xml --cov-report=term
```

`pytest-cov` must be installed — add it to `requirements.txt` (or a dev
requirements file installed by [pythonBuild](pythonBuild.md)):

```
pytest==8.3.4
pytest-cov==6.0.0
```

Sample log tail:

```
---------- coverage: platform linux, python 3.12.8 -----------
Name                 Stmts   Miss  Cover
----------------------------------------
app/api.py              88      9    90%
app/orders.py          120     27    78%
----------------------------------------
TOTAL                  208     36    83%
```

## How it fits

Called by [testApp](testApp.md) when `cfg.buildTool == 'python'`.

## Source

[`vars/pythonTest.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/pythonTest.groovy)
