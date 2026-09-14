# scanIac

Security scan over infrastructure code. Trivy's config scanner covers
Terraform and CloudFormation (it absorbed tfsec), so this reuses a tool
already in the toolbox rather than adding one.

## Syntax

```groovy
scanIac(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `infra.workingDir` | `.` | `terraform` | Directory scanned. |
| `quality.trivyFailOn` | `[HIGH, CRITICAL]` | `[CRITICAL]` | Severities that fail the build. |

## Returns

Nothing. Writes and archives `trivy-iac.json` and `trivy-iac.txt`, and sets
commit status `ci/iac-scan`. Fails the build with
`Trivy found HIGH,CRITICAL misconfigurations in terraform. See trivy-iac.txt`.

## Examples

```yaml
# .ci/config.yaml — from examples/terraform-stack
buildTool: terraform
infra:
  workingDir: terraform
quality:
  trivy: true
  trivyFailOn: [HIGH, CRITICAL]
```

```groovy
scanIac(cfg)
```

Runs:

```bash
trivy config --format json  --output trivy-iac.json terraform --no-progress || true
trivy config --format table --output trivy-iac.txt  terraform --no-progress || true
trivy config --severity HIGH,CRITICAL --exit-code 1 --no-progress --quiet terraform
```

Sample `trivy-iac.txt`:

```
main.tf (terraform)
Tests: 42 (SUCCESSES: 40, FAILURES: 2)
Failures: 2 (HIGH: 1, CRITICAL: 1)

AVD-AWS-0107 (CRITICAL): Security group rule allows ingress from public internet.
AVD-AWS-0089 (HIGH): Bucket does not have logging enabled
```

Ignoring a finding inline in Terraform:

```hcl
#trivy:ignore:AVD-AWS-0089
resource "aws_s3_bucket" "scratch" {
  bucket = "acme-scratch"
}
```

## How it fits

Called from [qualityChecks](qualityChecks.md) instead of
[scanTrivy](scanTrivy.md) when [isInfraRepo](../cloud/isInfraRepo.md) is
true — infra repos have no dependency tree to scan, so what matters is
whether the declared resources are misconfigured.

## Source

[`vars/scanIac.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/scanIac.groovy)
