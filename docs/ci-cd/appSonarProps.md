# appSonarProps

Language-specific properties for `sonar-scanner`.

## Syntax

```groovy
appSonarProps(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; only `cfg.buildTool` is read. |

## Returns

A `Map` of Sonar property → value for `cfg.buildTool`, or `[:]`:

| `buildTool` | Properties |
|---|---|
| `go` | `sonar.go.coverage.reportPaths=coverage.out` |
| `python` | `sonar.python.coverage.reportPaths=coverage.xml` |
| `maven` | `sonar.java.binaries=target/classes`<br>`sonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml`<br>`sonar.junit.reportPaths=target/surefire-reports` |
| `gradle` | `sonar.java.binaries=build/classes`<br>`sonar.coverage.jacoco.xmlReportPaths=build/reports/jacoco/test/jacocoTestReport.xml` |
| `npm` | `sonar.javascript.lcov.reportPaths=coverage/lcov.info` |
| others | none |

## Examples

```groovy
appSonarProps([buildTool: 'python'])
// → ['sonar.python.coverage.reportPaths': 'coverage.xml']

appSonarProps([buildTool: 'terraform'])
// → [:]
```

## How it fits

Merged into the flag list built by [sonarProperties](sonarProperties.md).

## Source

[`vars/appSonarProps.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/appSonarProps.groovy)
