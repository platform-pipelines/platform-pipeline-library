# cfnPackage

Runs `aws cloudformation package`, which uploads local artifacts (Lambda
zips, nested stack bodies) to S3 and rewrites the template to point at them.
Skipped when no `artifactBucket` is configured, since templates with no
local references don't need it.

## Syntax

```groovy
cfnPackage(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `infra.artifactBucket` | `null` | `acme-cfn-artifacts` | Unset → step is skipped. |
| `infra.template` | `template.yaml` | `templates/root.yaml` | Template to package. |
| `appName` | — | `billing-stack` | First part of the S3 prefix. |
| `infra.region` / `assumeRole` / `awsCredentialsId` | see [withAwsCredentials](withAwsCredentials.md) | | AWS access. |

## Returns

Nothing. Writes and archives `packaged-template.yaml`. With no
`infra.artifactBucket` it logs `No infra.artifactBucket — skipping cloudformation package`
and returns.

## Examples

```yaml
# .ci/config.yaml — from examples/cloudformation-stack
appName: billing-stack
buildTool: cloudformation
infra:
  template: templates/root.yaml
  artifactBucket: acme-cfn-artifacts
  region: us-east-1
```

```groovy
cfnPackage(cfg)
```

Runs (with `APP_VERSION=1.4.0`):

```bash
aws cloudformation package \
  --template-file templates/root.yaml \
  --s3-bucket acme-cfn-artifacts \
  --s3-prefix billing-stack/1.4.0 \
  --output-template-file packaged-template.yaml
```

A local reference such as `CodeUri: ./lambda/` in `root.yaml` becomes
`CodeUri: s3://acme-cfn-artifacts/billing-stack/1.4.0/9f2c…` in
`packaged-template.yaml`.

## How it fits

Dispatched from [packageApp](../ci-cd/packageApp.md) when
`cfg.buildTool == 'cloudformation'`. If `packaged-template.yaml` exists,
[cfnChangeSet](cfnChangeSet.md) deploys it instead of `infra.template`.

## Source

[`vars/cfnPackage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/cfnPackage.groovy)
