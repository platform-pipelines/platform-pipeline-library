# usingToolbox

Reports whether the current agent already carries every toolchain — the
`ci-toolbox` image (see `toolbox/`).

!!! note "The whole point of the toolbox"
    Set `CI_TOOLBOX=true` on toolbox-image agents via the node's environment
    in JCasC. When true, build steps run directly on the agent instead of
    being wrapped in a per-language container: no docker socket on the
    agent, one image pull instead of six.

## Syntax

```groovy
usingToolbox()
```

## Parameters

None. Reads the environment variable below.

| Environment variable | Required | Default | Sample value |
|---|---|---|---|
| `CI_TOOLBOX` | no | unset | `true` |

## Returns

`true` only when `env.CI_TOOLBOX` is exactly the string `'true'`; otherwise
`false`.

| `CI_TOOLBOX` | Result |
|---|---|
| `true` | `true` |
| unset, `false`, `TRUE`, `1` | `false` |

## Examples

```groovy
if (usingToolbox()) {
    sh 'trivy --version'
} else {
    docker.image('aquasec/trivy:latest').inside("--entrypoint=''") {
        sh 'trivy --version'
    }
}
```

Marking an agent as a toolbox agent (JCasC):

```yaml
jenkins:
  nodes:
    - permanent:
        name: toolbox-agent-1
        labelString: "linux docker toolbox"
        nodeProperties:
          - envVars:
              env:
                - key: CI_TOOLBOX
                  value: "true"
```

## How it fits

Checked by [inBuildContainer](inBuildContainer.md) and
[inToolContainer](inToolContainer.md) before deciding whether to wrap a step
in a container.

## Source

[`vars/usingToolbox.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/usingToolbox.groovy)
