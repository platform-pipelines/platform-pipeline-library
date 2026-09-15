# checkCoverage

Fails the build when coverage is below the configured minimum. Skipped
entirely when `quality.minCoverage` is unset.

## Syntax

```groovy
checkCoverage(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `quality.minCoverage` | `null` (gate off) | `75` | Minimum line coverage percentage, 0–100. |
| `buildTool` | — | `python` | Picks the report via [appCoverageFile](../pipelines/appCoverageFile.md). |

## Returns

Nothing. Outcomes:

| Situation | Result |
|---|---|
| `minCoverage` unset | returns silently |
| report missing or unreadable | `[WARN]  Could not read coverage from coverage.xml — skipping gate` |
| coverage ≥ minimum | commit status `ci/coverage` = success (`82.4%`) |
| coverage < minimum | commit status `ci/coverage` = failure (`71.2% < 75%`), build fails: `Coverage 71.2% is below the required 75%` |

## Examples

```yaml
buildTool: python
quality:
  minCoverage: 75
```

```groovy
checkCoverage(cfg)
```

Output:

```
[INFO]  Coverage 82.4% (minimum 75%)
[AUDIT] quality.coverage [coverage:82.4, minimum:75]
```

Raising the bar for one run without editing the YAML:

```groovy
standardPipeline([quality: [minCoverage: 90]])
```

## How it fits

Reads the percentage from [coveragePercent](coveragePercent.md). Called from
the `post { always }` of [standardPipeline](../pipelines/standardPipeline.md)'s `Test`
stage, so it runs even when tests fail.

## Source

[`vars/checkCoverage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/checkCoverage.groovy)
