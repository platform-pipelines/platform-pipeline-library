# appCoverageFile

Coverage report path the gate and Sonar both read, or `null`.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; only `cfg.buildTool` is read. |

## Returns

The coverage report path for `cfg.buildTool` (e.g. `coverage.xml` for
`python`), or `null` if none applies.

## Usage

```groovy
def report = appCoverageFile(cfg)
```

Used by [coveragePercent](coveragePercent.md) and [checkCoverage](checkCoverage.md).

## Source

[`vars/appCoverageFile.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/appCoverageFile.groovy)
