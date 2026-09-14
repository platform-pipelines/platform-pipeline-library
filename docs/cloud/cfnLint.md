# cfnLint

CloudFormation lint: `cfn-lint` over every template (see
[cfnTemplates](cfnTemplates.md)), then a syntax check by the service itself
(see [cfnBuild](cfnBuild.md)).

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.infra.workingDir` and `cfg.lint.failOnError`. |

## Returns

Nothing — errors if `cfn-lint` fails and `cfg.lint.failOnError` is true.

## Usage

```groovy
cfnLint(cfg)
```

## Source

[`vars/cfnLint.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/cfnLint.groovy)
