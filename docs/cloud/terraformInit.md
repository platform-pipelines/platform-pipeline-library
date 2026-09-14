# terraformInit

Runs `terraform init` with the environment's backend configuration.
`-input=false` everywhere: an interactive prompt in CI hangs until the build
times out, which looks like a stuck agent rather than a config error.

## Signature

```groovy
def call(Map cfg, Map envCfg = [:])
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.infra.workingDir`/`backendConfig`. |
| `envCfg` | `Map` | Target environment config; `envCfg.backendConfig`/`workspace` override `cfg`'s (default `[:]`). |

## Returns

Nothing — runs `terraform init` and selects/creates the workspace if
`envCfg.workspace` is given.

## Usage

```groovy
terraformInit(cfg, envCfg)
```

Called by [terraformBuild](terraformBuild.md) and [terraformPlan](terraformPlan.md).

## Source

[`vars/terraformInit.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/terraformInit.groovy)
