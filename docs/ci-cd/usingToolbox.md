# usingToolbox

Reports whether the current agent already carries every toolchain — the
`ci-toolbox` image (see `toolbox/`).

!!! note "The whole point of the toolbox"
    Set `CI_TOOLBOX=true` on toolbox-image agents via the node's environment
    in JCasC. When true, build steps run directly on the agent instead of
    being wrapped in a per-language container: no docker socket on the
    agent, one image pull instead of six.

## Signature

```groovy
def call()
```

## Returns

`true` if `env.CI_TOOLBOX` is set to `'true'` on this agent, `false`
otherwise.

## Usage

```groovy
if (usingToolbox()) { ... } else { docker.image(...).inside { ... } }
```

Checked by [inBuildContainer](inBuildContainer.md) and
[inToolContainer](inToolContainer.md) before deciding whether to wrap a step
in a container.

## Source

[`vars/usingToolbox.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/usingToolbox.groovy)
