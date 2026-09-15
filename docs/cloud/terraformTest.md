# terraformTest

Native `terraform test`. Tests should use `mock_provider` so they need no AWS
credentials and no state, and run on every branch. Policy checks
(`infra.policyDir`) are **not** run here: they need a real plan against real
state, so [terraformPlan](terraformPlan.md) runs them against each
environment's plan, before that plan can be approved.

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

## Returns

Nothing. Fails the build if a test fails.

| Condition | What runs |
|---|---|
| `tests/` exists or a `*.tftest.hcl` is in `workingDir` | `terraform init -backend=false` → `terraform test -no-color` |
| otherwise | logs `No .tftest.hcl files — skipping terraform test` |

## Examples

```groovy
terraformTest(cfg)
```

A sample `tests/network.tftest.hcl` (from examples/terraform-stack):

```hcl
mock_provider "aws" {
  mock_data "aws_availability_zones" {
    defaults = { names = ["eu-west-1a", "eu-west-1b"] }
  }
}

variables {
  environment = "dev"
  owner       = "platform-networking"
  vpc_cidr    = "10.10.0.0/16"
}

run "subnets_per_az" {
  command = plan
  assert {
    condition     = length(aws_subnet.private) == 2
    error_message = "expected one private subnet per AZ"
  }
}
```

## How it fits

Dispatched from [testApp](../ci-cd/testApp.md) when
`cfg.buildTool == 'terraform'`.

## Source

[`vars/terraformTest.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/terraformTest.groovy)
