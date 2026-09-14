# appLintReport

Lint report glob, for surfacing in the build UI.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; only `cfg.buildTool` is read. |

## Returns

The lint report glob for `cfg.buildTool` (e.g. `eslint-report.xml` for
`npm`), or `null` if none applies.

## Usage

```groovy
def glob = appLintReport(cfg)
```

Used by [archiveLintReports](archiveLintReports.md).

## Source

[`vars/appLintReport.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/appLintReport.groovy)
