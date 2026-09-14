# goTest

Runs the Go test suite with the race detector on by default — concurrency
bugs are cheap to catch here and expensive to catch in production.

## Syntax

```groovy
goTest(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; unused, kept so every `*Test` step has the same signature. |

## Returns

Nothing. Writes:

| File | Used by |
|---|---|
| `coverage.out` | [checkCoverage](checkCoverage.md), Sonar |
| `test-results.xml` | JUnit results in Jenkins |
| `go-test.out` | raw test output (also printed to the log) |

Fails the build if any test fails.

## Examples

```groovy
goTest(cfg)
```

Runs:

```bash
go test ./... -race -coverprofile=coverage.out -covermode=atomic > go-test.out 2>&1 || true
cat go-test.out
go run github.com/jstemmer/go-junit-report/v2@latest < go-test.out > test-results.xml || true
go test ./... -race        # second run decides pass/fail
```

The suite runs twice: once to capture reports whatever the outcome, and once
more for an unambiguous exit code.

Adding a coverage gate:

```yaml
buildTool: go
quality:
  minCoverage: 75
```

## How it fits

Called by [testApp](testApp.md) when `cfg.buildTool == 'go'`.

## Source

[`vars/goTest.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/goTest.groovy)
