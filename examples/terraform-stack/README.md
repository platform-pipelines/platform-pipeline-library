# terraform-stack example

An infrastructure repo built with Terraform. Setting `buildTool: terraform`
switches the pipeline into its infra shape: no image, no SBOM, no publish, and
deploy becomes **plan → approve → apply**, where apply consumes the exact saved
plan that was approved.

## Using this in a real repo

1. Copy `Jenkinsfile` to the root of your repo.
2. Keep `.ci/config.yaml` (already in place), then set `appName`,
   `infra.workingDir`, the var files, backend configs, and role ARNs.
3. Make sure the `aws-credentials` credential (and `github-token` for PR
   comments) exists on the controller — see the root README's Credentials
   table.

## What each stage runs

| Stage | Step | What happens |
|---|---|---|
| Lint | `terraformLint` | `terraform fmt -check`, `validate`, and `tflint` when `.tflint.hcl` exists |
| Build | `terraformBuild` | `terraform init` + `validate` against the real backend |
| Test | `terraformTest` | `terraform test` if tests exist; `conftest` against a plan when `infra.policyDir` is set |
| Quality | `scanIac` | `trivy config` over `infra.workingDir` |
| Deploy | `deployTerraform` | per environment: plan → PR summary → approval (if required) → apply the saved plan |

## What each section demonstrates

- **infra** — shared settings: working directory, common var files, policy
  directory and default region.
- **environments** — each one picks its own workspace, backend config and var
  files. `prod` assumes a dedicated deploy role and requires approval.
- **quality** — Sonar off (little value for HCL), Trivy config scan and secret
  scan on.
- **notify** — `on: always`, because every infra change is worth announcing.
