# terraformTest

Native `terraform test` plus OPA policy checks over the plan. Policy checks
live here rather than in the security stage because a policy failure is a
logic error in the change, not a vulnerability.

## Syntax

```groovy
terraformTest(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `infra.workingDir` | `.` | `terraform` | Directory Terraform runs in. |
| `infra.policyDir` | `null` | `policies` | Conftest/OPA policies, relative to `workingDir`. Unset or missing → policy check skipped. |

## Returns

Nothing. Fails the build if `terraform test` or `conftest` fails.

| Condition | What runs |
|---|---|
| `tests/` exists or a `*.tftest.hcl` is in `workingDir` | `terraform test -no-color` |
| otherwise | logs `No .tftest.hcl files — skipping terraform test` |
| `infra.policyDir` set and exists | `terraform plan -out=policy.tfplan` → `terraform show -json` → `conftest test --policy <policyDir> policy-plan.json` |

## Examples

```yaml
# from examples/terraform-stack
infra:
  workingDir: terraform
  policyDir: policies
```

```
terraform/
├── main.tf
├── tests/
│   └── vpc.tftest.hcl
└── policies/
    └── tags.rego
```

```groovy
terraformTest(cfg)
```

A sample `tests/vpc.tftest.hcl`:

```hcl
run "vpc_cidr" {
  command = plan
  assert {
    condition     = aws_vpc.main.cidr_block == "10.0.0.0/16"
    error_message = "unexpected VPC CIDR"
  }
}
```

A sample `policies/tags.rego` (fails when a new resource has no `owner` tag):

```rego
package main

deny contains msg if {
  rc := input.resource_changes[_]
  rc.change.actions[_] == "create"
  not rc.change.after.tags.owner
  msg := sprintf("%s is missing the 'owner' tag", [rc.address])
}
```

Conftest failure output:

```
FAIL - policy-plan.json - main - aws_s3_bucket.logs is missing the 'owner' tag
```

!!! note
    The policy plan runs against the default workspace with no var files, so
    policies should not depend on environment-specific values.

## How it fits

Dispatched from [testApp](../ci-cd/testApp.md) when
`cfg.buildTool == 'terraform'`.

## Source

[`vars/terraformTest.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/terraformTest.groovy)
