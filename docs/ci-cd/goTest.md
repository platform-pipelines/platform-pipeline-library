# goTest

Runs the Go test suite with the race detector on by default — concurrency
bugs are cheap to catch here and expensive to catch in production.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config (unused; kept for dispatcher parity). |

## Returns

Nothing — writes `coverage.out` and `test-results.xml`, and fails the build
on any test failure (tests run twice: once for the JUnit report, once more
for an unambiguous exit code).

## Usage

```groovy
goTest(cfg)
```

Called by [testApp](testApp.md) when `cfg.buildTool == 'go'`.

## Source

[`vars/goTest.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/goTest.groovy)
