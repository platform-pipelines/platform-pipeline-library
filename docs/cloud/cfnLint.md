# cfnLint

CloudFormation lint: `cfn-lint` over every template found by
[cfnTemplates](cfnTemplates.md). The service-side check runs later, in
[cfnBuild](cfnBuild.md).

## Syntax

```groovy
cfnLint(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `infra.templates` | `null` | `[templates/root.yaml]` | Explicit template list. |
| `infra.workingDir` | `.` | `templates` | Globbed when `infra.templates` is unset. |
| `lint.failOnError` | `true` | `false` | `false` = report problems but keep the build green. |

## Returns

Nothing. Writes `cfn-lint-report.xml` (JUnit) and prints `cfn-lint`'s normal
output to the log. Fails the build when:

- no templates are found: `No CloudFormation templates found under templates`, or
- `cfn-lint` reports problems and `lint.failOnError` is `true`: `cfn-lint reported problems`.

## Examples

```yaml
infra:
  workingDir: templates
lint:
  failOnError: true
```

```groovy
cfnLint(cfg)
```

Runs:

```bash
cfn-lint templates/network.yaml templates/root.yaml --format junit > cfn-lint-report.xml
cfn-lint templates/network.yaml templates/root.yaml
```

Sample failure output:

```
E3012 Property Resources/Bucket/Properties/VersioningConfiguration/Status should be of type String
templates/root.yaml:14:9
```

Report-only while a repo cleans up existing warnings:

```yaml
lint:
  failOnError: false
```

## How it fits

Dispatched from [lintApp](../ci-cd/lintApp.md) when
`cfg.buildTool == 'cloudformation'`. The JUnit report is archived by
[archiveLintReports](../ci-cd/archiveLintReports.md).

## Source

[`vars/cfnLint.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/cfnLint.groovy)
