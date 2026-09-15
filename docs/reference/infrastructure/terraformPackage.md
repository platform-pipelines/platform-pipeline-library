# terraformPackage

Infra repos produce no build artifact — the plan is the artifact, and it's
created and archived during deploy (see [terraformPlan](terraformPlan.md))
where it's bound to a specific environment.

## Syntax

```groovy
terraformPackage(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; unused, kept so every `*Package` step has the same signature. |

## Returns

Nothing. Only logs a line.

## Examples

```groovy
terraformPackage(cfg)
```

Output:

```
[INFO]  Terraform: the plan is the artifact, produced per environment at deploy time
```

## How it fits

Dispatched from [packageApp](../pipelines/packageApp.md) when
`cfg.buildTool == 'terraform'`.

## Source

[`vars/terraformPackage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/terraformPackage.groovy)
