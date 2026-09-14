# terraformPackage

Infra repos produce no build artifact — the plan is the artifact, and it's
created and archived during deploy (see [terraformPlan](terraformPlan.md))
where it's bound to a specific environment.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; unused, kept for `call()` signature consistency across `buildTool`s. |

## Returns

Nothing — logs that packaging is a no-op for Terraform.

## Usage

```groovy
terraformPackage(cfg)
```

Dispatched from `packageApp` when `cfg.buildTool == 'terraform'`.

## Source

[`vars/terraformPackage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/terraformPackage.groovy)
