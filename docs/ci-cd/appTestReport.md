# appTestReport

JUnit XML glob, or `null` when the toolchain produces none.

## Syntax

```groovy
appTestReport(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; only `cfg.buildTool` is read. |

## Returns

A JUnit XML glob `String` for `cfg.buildTool`, or `null`:

| `buildTool` | JUnit glob | Written by |
|---|---|---|
| `go` | `test-results.xml` | [goTest](goTest.md) |
| `python` | `test-results.xml` | [pythonTest](pythonTest.md) |
| `maven` | `target/surefire-reports/*.xml` | [mavenTest](mavenTest.md) |
| `gradle` | `build/test-results/test/*.xml` | [gradleTest](gradleTest.md) |
| `npm` | `junit.xml` | your `npm test` script (e.g. `jest-junit`) |
| `cloudformation` | `checkov-report.xml` | [cfnTest](../cloud/cfnTest.md) |
| `terraform`, `docker-only` | `null` | — |

Lint reports are never returned here — they are archived separately by
[archiveLintReports](archiveLintReports.md).

## Examples

```groovy
appTestReport([buildTool: 'maven'])       // → 'target/surefire-reports/*.xml'
appTestReport([buildTool: 'terraform'])   // → null
```

```groovy
post {
    always {
        script {
            def reports = appTestReport(cfg)
            if (reports) { junit allowEmptyResults: true, testResults: reports }
        }
    }
}
```

## How it fits

Used by [standardPipeline](standardPipeline.md)'s `Test` stage to publish
test results.

## Source

[`vars/appTestReport.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/appTestReport.groovy)
