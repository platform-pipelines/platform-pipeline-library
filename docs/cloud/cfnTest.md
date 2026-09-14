# cfnTest

Policy checks over the templates. CloudFormation has no plan-time test
framework, so this is static analysis with [checkov](https://www.checkov.io/)
for common misconfigurations (unencrypted buckets, open security groups,
missing logging, …).

## Syntax

```groovy
cfnTest(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `infra.workingDir` | `.` | `templates` | Directory checkov scans. |
| `lint.failOnError` | `true` | `false` | `false` = violations are reported but don't fail the build. |

## Returns

Nothing. Prints checkov's CLI output, writes and archives
`checkov-report.xml` (JUnit), and fails the build with
`checkov found policy violations in the CloudFormation templates` when checkov
reports failures and `lint.failOnError` is `true`.

## Examples

```yaml
infra:
  workingDir: templates
```

```groovy
cfnTest(cfg)
```

Runs:

```bash
checkov -d templates --framework cloudformation \
  --output cli --output junitxml --output-file-path console,checkov-report.xml --quiet
```

Sample output:

```
Check: CKV_AWS_18: "Ensure the S3 bucket has access logging enabled"
    FAILED for resource: AWS::S3::Bucket.InvoicesBucket
    File: /root.yaml:12-20
```

Skipping one check for a resource, in the template itself:

```yaml
InvoicesBucket:
  Type: AWS::S3::Bucket
  Metadata:
    checkov:
      skip:
        - id: CKV_AWS_18
          comment: Access logs go to the central logging account
```

## How it fits

Dispatched from [testApp](../ci-cd/testApp.md) when
`cfg.buildTool == 'cloudformation'`. [appTestReport](../ci-cd/appTestReport.md)
returns `checkov-report.xml`, so the results appear as JUnit tests in Jenkins.

## Source

[`vars/cfnTest.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/cfnTest.groovy)
