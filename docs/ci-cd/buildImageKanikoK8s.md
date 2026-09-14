# buildImageKanikoK8s

Runs Kaniko as a sidecar container in the agent pod — the only genuinely
unprivileged image-building option, with no docker socket anywhere.

!!! note "Requires a pod template"
    Needs the `kubernetes` plugin and a pod template that declares a
    container named `kaniko` — see `local/casc/jenkins.yaml` for an example.

## Syntax

```groovy
buildImageKanikoK8s(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; passed to [kanikoArgs](kanikoArgs.md). |

### Config keys read (via kanikoArgs)

| Key | Default | Sample value |
|---|---|---|
| `imageRepo` | — | `ghcr.io/acme/catalog-service` |
| `dockerfile` | `Dockerfile` | `Dockerfile` |
| `appName` | — | `catalog-service` |

## Returns

Nothing. Builds and pushes the image from the pod's `kaniko` container;
Kaniko writes `image-digest.txt`.

## Examples

```yaml
# .ci/config.yaml — from examples/java-service
imageRepo: ghcr.io/acme/catalog-service
imageBuilder: kaniko-k8s
```

A pod template with the required container (JCasC):

```yaml
jenkins:
  clouds:
    - kubernetes:
        name: k8s
        namespace: jenkins
        templates:
          - name: kaniko
            label: "linux kaniko"
            containers:
              - name: jnlp
                image: jenkins/inbound-agent:latest
              - name: kaniko
                image: gcr.io/kaniko-project/executor:v1.23.2-debug
                command: /busybox/cat
                ttyEnabled: true
```

```groovy
buildImageKanikoK8s(cfg)
```

Runs inside the `kaniko` container:

```bash
/kaniko/executor --context=dir://$(pwd) \
  --dockerfile=Dockerfile \
  --destination=ghcr.io/acme/catalog-service:1.4.0 \
  --destination=ghcr.io/acme/catalog-service:ab12cd3 \
  --destination=ghcr.io/acme/catalog-service:latest \
  ... --digest-file=image-digest.txt
```

!!! warning "Agent label"
    [standardPipeline](standardPipeline.md) runs the Package stage on
    `label 'linux'`. The pod template that provides the `kaniko` container
    must match that label.

## How it fits

Called by [buildImage](buildImage.md) when `cfg.imageBuilder == 'kaniko-k8s'`,
after [kanikoDockerConfig](kanikoDockerConfig.md).

## Source

[`vars/buildImageKanikoK8s.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/buildImageKanikoK8s.groovy)
