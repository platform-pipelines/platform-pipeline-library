# appToolImage

Container the build runs in.

When the agent is the toolbox image (`CI_TOOLBOX=true`) every toolchain is
already on `PATH`, so steps run in place and this is never consulted. These
per-language images are the fallback for agents that aren't the toolbox.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.buildTool` and `cfg.runtimeVersion`. |

## Returns

The container image tag for `cfg.buildTool` (e.g. `golang:1.27`). Throws if
`buildTool` is unsupported.

## Usage

```groovy
def image = appToolImage(cfg)
```

Used by [inBuildContainer](inBuildContainer.md) to pick the fallback image.

## Source

[`vars/appToolImage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/appToolImage.groovy)
