# appTestReport

JUnit XML glob, or `null` when the toolchain produces none.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; only `cfg.buildTool` is read. |

## Returns

The JUnit XML glob for `cfg.buildTool`, or `null` if none applies.
Lint reports are never returned here — they are archived separately by
[archiveLintReports](archiveLintReports.md). For `cloudformation` this is
`checkov-report.xml` from [cfnTest](../cloud/cfnTest.md); `terraform` and
`docker-only` produce no JUnit report.

## Usage

```groovy
def glob = appTestReport(cfg)
```

## Source

[`vars/appTestReport.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/appTestReport.groovy)
