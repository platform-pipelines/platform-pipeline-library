# inToolContainer

Runs a scanner body in the right place for this agent: directly, on a
toolbox agent, or inside the scanner's own image otherwise.

## Signature

```groovy
def call(String image, Closure body)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `image` | `String` | Scanner image to fall back to off the toolbox. |
| `body` | `Closure` | Scanner steps to run. |

## Returns

Nothing — runs `body()` in place or inside the scanner's own image.

## Usage

```groovy
inToolContainer('aquasec/trivy:latest') { sh 'trivy image ...' }
```

Checks [usingToolbox](usingToolbox.md) first. Used by
[generateSbom](generateSbom.md) and the `scan*` steps.

## Source

[`vars/inToolContainer.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/inToolContainer.groovy)
