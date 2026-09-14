# gradleTest

Runs a Gradle project's test suite with JaCoCo coverage.

## Syntax

```groovy
gradleTest(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; unused, kept so every `*Test` step has the same signature. |

## Returns

Nothing. Fails the build on a test failure. Produces:

| File | Used by |
|---|---|
| `build/test-results/test/*.xml` | JUnit results in Jenkins |
| `build/reports/jacoco/test/jacocoTestReport.xml` | [checkCoverage](checkCoverage.md), Sonar |

## Examples

```groovy
gradleTest(cfg)
```

Runs:

```bash
gradle --no-daemon --console=plain test jacocoTestReport
```

The project must apply `jacoco` and enable the XML report:

```kotlin
// build.gradle.kts
plugins { jacoco }

tasks.jacocoTestReport {
    reports { xml.required.set(true) }
}
```

## How it fits

Called by [testApp](testApp.md) when `cfg.buildTool == 'gradle'`.

## Source

[`vars/gradleTest.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/gradleTest.groovy)
