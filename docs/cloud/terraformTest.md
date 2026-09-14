# terraformTest

Native `terraform test` plus OPA policy checks over the plan. Policy checks
live here rather than in the security stage because a policy failure is a
logic error in the change, not a vulnerability.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.infra.workingDir`/`policyDir`. |

## Returns

Nothing — runs `terraform test` if `.tftest.hcl` files are present, and
`conftest` against a fresh plan if `cfg.infra.policyDir` is set and exists.

## Usage

```groovy
terraformTest(cfg)
```

## Source

[`vars/terraformTest.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/terraformTest.groovy)
