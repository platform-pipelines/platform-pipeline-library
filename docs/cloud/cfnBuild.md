# cfnBuild

Asks CloudFormation itself to validate each template. `cfn-lint` (see
[cfnLint](cfnLint.md)) catches more, but only the service knows what the
service will actually accept.

## Syntax

```groovy
cfnBuild(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `infra.templates` | `null` | `[templates/root.yaml, templates/network.yaml]` | Explicit template list (see [cfnTemplates](cfnTemplates.md)). |
| `infra.workingDir` | `.` | `templates` | Globbed for templates when `infra.templates` is unset. |
| `infra.region` | `us-east-1` | `us-east-1` | AWS region. |
| `infra.assumeRole` | `null` | `arn:aws:iam::444455556666:role/JenkinsValidate` | Role to assume, if any. |
| `infra.awsCredentialsId` | `aws-credentials` | `aws-credentials` | Base AWS credential. |

## Returns

Nothing. Fails the build on the first template CloudFormation rejects.

## Examples

```yaml
# .ci/config.yaml — from examples/cloudformation-stack
appName: billing-stack
buildTool: cloudformation
infra:
  workingDir: templates
  region: us-east-1
```

```groovy
cfnBuild(cfg)
```

Runs, for each template found:

```bash
aws cloudformation validate-template --template-body file://templates/root.yaml > /dev/null
```

Log output:

```
====================================================================
  Build: CloudFormation
====================================================================
[INFO]  Validated templates/network.yaml
[INFO]  Validated templates/root.yaml
```

## How it fits

Dispatched from [buildApp](../ci-cd/buildApp.md) when
`cfg.buildTool == 'cloudformation'`. Credentials come from
[withAwsCredentials](withAwsCredentials.md).

## Source

[`vars/cfnBuild.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/cfnBuild.groovy)
