# coveragePercent

Overall line coverage for this repo, or `-1` if it cannot be determined.

## Syntax

```groovy
coveragePercent(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; `buildTool` locates the report via [appCoverageFile](appCoverageFile.md). |

## Returns

Line coverage as a `BigDecimal` rounded to one decimal place (e.g. `82.4`),
or `-1` when the tool has no coverage report, the file doesn't exist, or it
can't be parsed.

Supported formats (detected from content, not the file name): Cobertura XML,
JaCoCo XML, lcov, Go coverprofile.

## Examples

```groovy
coveragePercent([buildTool: 'python'])    // coverage.xml present → 82.4
coveragePercent([buildTool: 'go'])        // coverage.out present → 67.9
coveragePercent([buildTool: 'terraform']) // → -1
```

Custom soft gate that marks the build unstable instead of failing it:

```groovy
def pct = coveragePercent(cfg)
if (pct >= 0 && pct < 60) {
    unstable "Coverage ${pct}% is under the 60% soft target"
}
```

The same parser on a laptop:

```bash
python3 resources/com/platformpipelines/scripts/coverage_percent.py coverage.xml
```

## How it fits

Parsing runs in `resources/com/platformpipelines/scripts/coverage_percent.py`
via [useScript](../other/useScript.md), not in Groovy. Used by
[checkCoverage](checkCoverage.md).

## Source

[`vars/coveragePercent.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/coveragePercent.groovy)
