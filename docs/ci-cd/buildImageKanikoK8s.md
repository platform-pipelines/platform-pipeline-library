# buildImageKanikoK8s

Runs Kaniko as a sidecar container in the agent pod — the only genuinely
unprivileged image-building option, with no docker socket anywhere.

!!! note "Requires a pod template"
    Needs the `kubernetes` plugin and a pod template that declares a
    container named `kaniko` — see `local/casc/jenkins.yaml` for an example.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; passed through to [kanikoArgs](kanikoArgs.md). |

## Returns

Nothing — runs Kaniko in the pod's `kaniko` sidecar container to build and
push the image.

## Usage

```groovy
buildImageKanikoK8s(cfg)
```

Called by [buildImage](buildImage.md) when `cfg.imageBuilder == 'kaniko-k8s'`.

## Source

[`vars/buildImageKanikoK8s.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/buildImageKanikoK8s.groovy)
