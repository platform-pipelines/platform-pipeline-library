# coveragePercent

Overall line coverage for this repo, or `-1` if it cannot be determined.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; passed to [`appCoverageFile(cfg)`](appCoverageFile.md) to locate the report. |

## Returns

Line coverage percentage as a `BigDecimal`, or `-1` if no report exists.

## Usage

```groovy
def pct = coveragePercent(cfg)
```

The actual parsing runs in `resources/com/company/scripts/coverage_percent.py`
via [useScript](../other/useScript.md), not in Groovy — see the root README's
design rules on why parsing logic lives in Python.

## Source

[`vars/coveragePercent.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/coveragePercent.groovy)
