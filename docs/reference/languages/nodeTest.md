# nodeTest

Runs the project's `npm test` script.

## Syntax

```groovy
nodeTest(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; unused, kept so every `*Test` step has the same signature. |

## Returns

Nothing. Fails the build if `npm test` exits non-zero.

## Examples

```groovy
nodeTest(cfg)          // runs: npm test
```

The pipeline expects these files from your test script:

| File | Used by | Without it |
|---|---|---|
| `junit.xml` | JUnit results in Jenkins | no test report |
| `coverage/lcov.info` | [checkCoverage](../quality/checkCoverage.md), Sonar | coverage gate skipped with a warning |

A Jest setup that writes both:

```json
{
  "scripts": {
    "test": "jest --ci --coverage --coverageReporters=lcov --reporters=default --reporters=jest-junit"
  },
  "devDependencies": {
    "jest": "^29.7.0",
    "jest-junit": "^16.0.0"
  }
}
```

`jest-junit` writes `junit.xml` in the working directory by default.

## How it fits

Called by [testApp](../pipelines/testApp.md) when `cfg.buildTool == 'npm'`.

## Source

[`vars/nodeTest.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/nodeTest.groovy)
