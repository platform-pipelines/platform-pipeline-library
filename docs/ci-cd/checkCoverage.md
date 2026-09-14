# checkCoverage

Fails the build when coverage is below the configured minimum. Skipped
entirely when `quality.minCoverage` is unset.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.quality.minCoverage`. |

## Returns

Nothing. Sets a GitHub commit status (`ci/coverage`) and throws if coverage
is below the minimum.

## Usage

```groovy
checkCoverage(cfg)
```

Reads the actual percentage from [coveragePercent](coveragePercent.md),
which in turn locates the report via [appCoverageFile](appCoverageFile.md).

## Source

[`vars/checkCoverage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/checkCoverage.groovy)
