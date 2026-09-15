# terraformLint

Terraform lint: formatting, then validation, then tflint. `fmt` runs first
because it's instant and catches the most common review comment.

## Syntax

```groovy
terraformLint(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `infra.workingDir` | `.` | `terraform` | Directory linted. |
| `lint.autoFormat` | `false` | `true` | Runs `terraform fmt -recursive` before checking. |
| `lint.failOnError` | `true` | `false` | `false` = formatting/tflint problems are reported but don't fail. |

## Returns

Nothing. Writes `tflint-report.xml` when tflint runs. Fails the build:

- always, if `terraform validate` fails;
- when `lint.failOnError` is `true` and `fmt -check` or tflint fails, with
  `Terraform lint failed. Run: terraform fmt -recursive`.

## Examples

```yaml
infra:
  workingDir: terraform
lint:
  failOnError: true
```

```groovy
terraformLint(cfg)
```

Runs inside `terraform/`:

```bash
terraform fmt -check -recursive -diff
terraform init -backend=false -input=false -no-color
terraform validate -no-color
# only when terraform/.tflint.hcl exists:
tflint --init
tflint --format junit > tflint-report.xml
tflint --format compact
```

Sample `fmt` failure:

```
main.tf
--- old/main.tf
+++ new/main.tf
@@ -3,3 +3,3 @@
-  cidr_block="10.0.0.0/16"
+  cidr_block = "10.0.0.0/16"
```

A minimal `.tflint.hcl` to turn tflint on:

```hcl
plugin "aws" {
  enabled = true
  version = "0.38.0"
  source  = "github.com/terraform-linters/tflint-ruleset-aws"
}
```

Validation runs with `-backend=false`, so it needs no credentials and no
state access — it's about syntax and types, not what's deployed.

## How it fits

Dispatched from [lintApp](../pipelines/lintApp.md) when
`cfg.buildTool == 'terraform'`.

## Source

[`vars/terraformLint.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/terraformLint.groovy)
