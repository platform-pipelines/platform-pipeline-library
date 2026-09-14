# terraformInit

Runs `terraform init` with the environment's backend configuration, and
selects (or creates) the environment's workspace. `-input=false` everywhere:
an interactive prompt in CI hangs until the build times out, which looks like
a stuck agent rather than a config error.

## Syntax

```groovy
terraformInit(Map cfg)                // shared backend, no workspace
terraformInit(Map cfg, Map envCfg)    // per-environment backend and workspace
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |
| `envCfg` | `Map` | no | `[:]` | Target environment; its `backendConfig` and `workspace` win over `cfg`'s. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `infra.workingDir` | `.` | `terraform` | Directory Terraform runs in. |
| `infra.backendConfig` | `null` | `backends/shared.hcl` | Fallback backend config file. |
| `environments[].backendConfig` | `null` | `backends/prod.hcl` | Backend config for this environment. |
| `environments[].workspace` | `null` | `prod` | Workspace to select or create. Unset → default workspace. |

## Returns

Nothing. Fails the build if `init` or workspace selection fails.

## Examples

```yaml
infra:
  workingDir: terraform
environments:
  - name: prod
    workspace: prod
    backendConfig: backends/prod.hcl
```

```groovy
terraformInit(cfg, cfg.environments[0])
```

Runs inside `terraform/`:

```bash
terraform init -input=false -no-color -backend-config=backends/prod.hcl -reconfigure
terraform workspace select -or-create prod
```

Without an environment, and with no `infra.backendConfig`:

```groovy
terraformInit(cfg)
// terraform init -input=false -no-color -reconfigure
```

A sample `backends/prod.hcl`:

```hcl
bucket         = "acme-terraform-state"
key            = "platform-network/terraform.tfstate"
region         = "eu-west-1"
dynamodb_table = "terraform-locks"
```

## How it fits

Called by [terraformBuild](terraformBuild.md) (no environment) and
[terraformPlan](terraformPlan.md) (per environment).

## Source

[`vars/terraformInit.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/terraformInit.groovy)
