# terraformBuild

For Terraform there is nothing to compile — `init` plus `validate` against
the real backend is the closest equivalent, and it catches backend and
provider problems before the plan stage.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.infra.workingDir`. |

## Returns

Nothing — runs [terraformInit](terraformInit.md) then `terraform validate`.

## Usage

```groovy
terraformBuild(cfg)
```

Dispatched from `buildApp` when `cfg.buildTool == 'terraform'`.

## Source

[`vars/terraformBuild.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/terraformBuild.groovy)
