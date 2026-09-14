# cfnBuild

Asks CloudFormation itself to validate each template. `cfn-lint` (see
[cfnLint](cfnLint.md)) catches more, but only the service knows what the
service will actually accept.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.infra` (region, credentials) and its templates via [cfnTemplates](cfnTemplates.md). |

## Returns

Nothing — errors if any template fails CloudFormation's own validation.

## Usage

```groovy
cfnBuild(cfg)
```

Dispatched from `buildApp` when `cfg.buildTool == 'cloudformation'`.

## Source

[`vars/cfnBuild.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/cfnBuild.groovy)
