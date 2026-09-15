# appCoverageFile

Coverage report path that both the coverage gate and Sonar read, or `null`.

## Syntax

```groovy
appCoverageFile(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; only `cfg.buildTool` is read. |

## Returns

A report path `String` for `cfg.buildTool`, or `null`:

| `buildTool` | Report | Format | Written by |
|---|---|---|---|
| `go` | `coverage.out` | Go coverprofile | [goTest](../languages/goTest.md) |
| `python` | `coverage.xml` | Cobertura | [pythonTest](../languages/pythonTest.md) |
| `maven` | `target/site/jacoco/jacoco.xml` | JaCoCo | [mavenTest](../languages/mavenTest.md) |
| `gradle` | `build/reports/jacoco/test/jacocoTestReport.xml` | JaCoCo | [gradleTest](../languages/gradleTest.md) |
| `npm` | `coverage/lcov.info` | lcov | your `npm test` script |
| `terraform`, `cloudformation`, `docker-only` | `null` | — | — |

## Examples

```groovy
appCoverageFile([buildTool: 'python'])     // → 'coverage.xml'
appCoverageFile([buildTool: 'terraform'])  // → null
```

For `npm`, make sure the test script writes lcov to that path, e.g. with Jest:

```json
{
  "scripts": {
    "test": "jest --coverage --coverageReporters=lcov --reporters=default --reporters=jest-junit"
  }
}
```

## How it fits

Used by [coveragePercent](../quality/coveragePercent.md) and [checkCoverage](../quality/checkCoverage.md).
[appSonarProps](appSonarProps.md) points Sonar at the same files.

## Source

[`vars/appCoverageFile.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/appCoverageFile.groovy)
