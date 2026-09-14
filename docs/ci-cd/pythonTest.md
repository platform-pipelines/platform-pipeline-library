# pythonTest

Runs the Python test suite with coverage and JUnit XML output.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; unused here, kept for `call()` signature parity across build tools. |

## Returns

Nothing — runs `pytest --junitxml=test-results.xml --cov=. --cov-report=xml --cov-report=term`.

## Usage

```groovy
pythonTest(cfg)
```

Called by [testApp](testApp.md) when `cfg.buildTool == 'python'`.

## Source

[`vars/pythonTest.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/pythonTest.groovy)
