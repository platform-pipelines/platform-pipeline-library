# terraformLint

Terraform lint: formatting, then validation, then tflint. `fmt` runs first
because it's instant and catches the most common review comment.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.infra.workingDir`, `cfg.lint.autoFormat`/`failOnError`. |

## Returns

Nothing — errors on a formatting/validation/tflint failure when
`cfg.lint.failOnError` is true.

## Usage

```groovy
terraformLint(cfg)
```

Validation runs with `-backend=false` so it needs no credentials and no
state access — it's about syntax and types, not what's deployed. `tflint`
only runs if a `.tflint.hcl` file is present.

## Source

[`vars/terraformLint.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/terraformLint.groovy)
