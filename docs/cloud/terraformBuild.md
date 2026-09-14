# terraformBuild

For Terraform there is nothing to compile — `init` plus `validate` against
the real backend is the closest equivalent, and it catches backend and
provider problems before the plan stage.

## Syntax

```groovy
terraformBuild(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `infra.workingDir` | `.` | `terraform` | Directory Terraform runs in. |
| `infra.backendConfig` | `null` | `backends/dev.hcl` | Passed as `-backend-config` to `init`. |

## Returns

Nothing. Fails the build if `init` or `validate` fails.

## Examples

```yaml
appName: platform-network
buildTool: terraform
infra:
  workingDir: terraform
  backendConfig: backends/dev.hcl
```

```groovy
terraformBuild(cfg)
```

Runs inside `terraform/`:

```bash
terraform init -input=false -no-color -backend-config=backends/dev.hcl -reconfigure
terraform validate -no-color
```

!!! note "Backend access"
    Unlike [terraformLint](terraformLint.md), which uses `-backend=false`,
    this step initialises the real backend, so the agent needs whatever
    credentials the backend requires (e.g. environment AWS credentials for
    an S3 backend).

## How it fits

Dispatched from [buildApp](../ci-cd/buildApp.md) when
`cfg.buildTool == 'terraform'`. Runs [terraformInit](terraformInit.md) with
no environment, so no workspace is selected.

## Source

[`vars/terraformBuild.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/terraformBuild.groovy)
