# appLintReport

Lint report glob, for surfacing in the build UI.

## Syntax

```groovy
appLintReport(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; only `cfg.buildTool` is read. |

## Returns

A report glob `String` for `cfg.buildTool`, or `null`:

| `buildTool` | Report | Written by |
|---|---|---|
| `go` | `golangci-report.xml` | [goLint](../languages/goLint.md) |
| `python` | `ruff-report.xml` | [pythonLint](../languages/pythonLint.md) |
| `maven` | `target/checkstyle-result.xml` | [mavenLint](../languages/mavenLint.md) |
| `gradle` | `build/reports/checkstyle/*.xml` | [gradleLint](../languages/gradleLint.md) |
| `npm` | `eslint-report.xml` | [nodeLint](../languages/nodeLint.md) |
| `terraform` | `tflint-report.xml` | [terraformLint](../infrastructure/terraformLint.md) |
| `cloudformation` | `cfn-lint-report.xml` | [cfnLint](../infrastructure/cfnLint.md) |
| `docker-only` | `null` | — |

## Examples

```groovy
appLintReport([buildTool: 'npm'])            // → 'eslint-report.xml'
appLintReport([buildTool: 'docker-only'])    // → null
```

## How it fits

Used by [archiveLintReports](../quality/archiveLintReports.md).

## Source

[`vars/appLintReport.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/appLintReport.groovy)
