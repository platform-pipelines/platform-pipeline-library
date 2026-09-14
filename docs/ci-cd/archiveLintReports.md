# archiveLintReports

Archives whatever lint report this toolchain produced.

Centralized so each lint step only has to know how to run its linter, not
how Jenkins stores artifacts.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; passed through to [`appLintReport(cfg)`](appLintReport.md). |

## Returns

Nothing. Archives the report artifact if one exists.

## Usage

```groovy
archiveLintReports(cfg)
```

## Source

[`vars/archiveLintReports.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/archiveLintReports.groovy)
