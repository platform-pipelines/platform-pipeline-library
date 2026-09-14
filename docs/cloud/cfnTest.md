# cfnTest

Policy checks over the templates. CloudFormation has no plan-time test
framework, so this is static analysis: [checkov](https://www.checkov.io/)
for common misconfigurations, cfn-guard for rules you write yourself.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.infra.workingDir` and `cfg.lint.failOnError`. |

## Returns

Nothing — archives `checkov-report.xml` and errors if checkov reports
violations and `cfg.lint.failOnError` is true.

## Usage

```groovy
cfnTest(cfg)
```

## Source

[`vars/cfnTest.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/cfnTest.groovy)
