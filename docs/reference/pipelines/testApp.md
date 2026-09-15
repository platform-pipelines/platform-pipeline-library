# testApp

Routes to the right test step for this repo's `buildTool`.

## Syntax

```groovy
testApp(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; `cfg.buildTool` selects the test step. |

## Returns

Nothing. Fails the build for an unknown tool:
`No test step for buildTool 'rust'`.

| `buildTool` | Step | What it runs | Reports |
|---|---|---|---|
| `go` | [goTest](../languages/goTest.md) | `go test ./... -race` | `test-results.xml`, `coverage.out` |
| `python` | [pythonTest](../languages/pythonTest.md) | `pytest --cov` | `test-results.xml`, `coverage.xml` |
| `maven` | [mavenTest](../languages/mavenTest.md) | `mvn verify -DskipITs` | Surefire XML, JaCoCo XML |
| `gradle` | [gradleTest](../languages/gradleTest.md) | `gradle test jacocoTestReport` | test-results XML, JaCoCo XML |
| `npm` | [nodeTest](../languages/nodeTest.md) | `npm test` | whatever the script writes (`junit.xml`, `coverage/lcov.info`) |
| `terraform` | [terraformTest](../infrastructure/terraformTest.md) | `terraform test`, conftest | — |
| `cloudformation` | [cfnTest](../infrastructure/cfnTest.md) | checkov | `checkov-report.xml` |
| `docker-only` | [dockerOnlyTest](../languages/dockerOnlyTest.md) | nothing | — |

## Examples

```yaml
buildTool: maven
```

```groovy
testApp(cfg)             // → mavenTest(cfg)
```

Running tests and always publishing results, as `standardPipeline` does:

```groovy
stage('Test') {
    steps {
        script { inBuildContainer(cfg) { testApp(cfg) } }
    }
    post {
        always {
            script {
                def reports = appTestReport(cfg)
                if (reports) { junit allowEmptyResults: true, testResults: reports }
                checkCoverage(cfg)
            }
        }
    }
}
```

## How it fits

Called from the `Test` stage of [standardPipeline](standardPipeline.md).

## Source

[`vars/testApp.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/testApp.groovy)
