# appToolImage

Container image the build runs in on a non-toolbox agent.

When the agent is the toolbox image (`CI_TOOLBOX=true`) every toolchain is
already on `PATH`, so steps run in place and this is never consulted. These
per-language images are the fallback for agents that aren't the toolbox.

## Syntax

```groovy
appToolImage(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `buildTool` | — | `python` | Picks the image. |
| `runtimeVersion` | `null` (per-tool default below) | `"3.12"` | Picks the image tag. Quote it in YAML so `3.10` doesn't become `3.1`. |

## Returns

An image reference `String`. Fails the build for an unknown `buildTool`:
`No tool image for buildTool '…'`.

| `buildTool` | Image | Default version | With `runtimeVersion` |
|---|---|---|---|
| `go` | `golang:<v>` | `1.27` | `"1.26"` → `golang:1.26` |
| `python` | `python:<v>-slim` | `3.12` | `"3.13"` → `python:3.13-slim` |
| `maven` | `maven:3.9-eclipse-temurin-<v>` | `21` | `"17"` → `maven:3.9-eclipse-temurin-17` |
| `gradle` | `gradle:9-jdk<v>` | `21` | `"17"` → `gradle:9-jdk17` |
| `npm` | `node:<v>-alpine` | `24` | `"20"` → `node:20-alpine` |
| `terraform` | `hashicorp/terraform:<v>` | `1.16` | `"1.15"` → `hashicorp/terraform:1.15` |
| `cloudformation` | `amazon/aws-cli:latest` | — | ignored |
| `docker-only` | `alpine:3.19` | — | ignored |

## Examples

```yaml
buildTool: npm
runtimeVersion: "20"
```

```groovy
appToolImage(cfg)                                  // → 'node:20-alpine'
appToolImage([buildTool: 'go'])                    // → 'golang:1.27'
```

## How it fits

Used by [inBuildContainer](../images/inBuildContainer.md) to pick the fallback image.

## Source

[`vars/appToolImage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/appToolImage.groovy)
