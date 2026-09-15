# configImageBuilders

Single source of truth for legal `imageBuilder` values.

## Syntax

```groovy
configImageBuilders()
```

## Parameters

None.

## Returns

`List<String>` — `['kaniko-docker', 'kaniko-k8s', 'buildah']`.

| Value | Implementation | Needs |
|---|---|---|
| `kaniko-docker` (default) | [buildImageKanikoDocker](../images/buildImageKanikoDocker.md) | a docker socket on the agent |
| `kaniko-k8s` | [buildImageKanikoK8s](../images/buildImageKanikoK8s.md) | `kubernetes` plugin + a pod template with a `kaniko` container |
| `buildah` | [buildImageBuildah](../images/buildImageBuildah.md) | toolbox agent with subuid/subgid and fuse-overlayfs |

## Examples

```groovy
configImageBuilders()                  // → ['kaniko-docker', 'kaniko-k8s', 'buildah']
'buildah' in configImageBuilders()     // → true
```

Choosing one in `.ci/config.yaml`:

```yaml
imageBuilder: kaniko-k8s
```

An unsupported value fails validation with:

```
imageBuilder 'docker' unsupported (use: kaniko-docker, kaniko-k8s, buildah)
```

## How it fits

Read by [`configValidate`](configValidate.md) and by
[buildImage](../images/buildImage.md)'s error message. Adding a builder means
adding it here, writing its `buildImage*` step, and adding a case to
`buildImage`.

## Source

[`vars/configImageBuilders.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configImageBuilders.groovy)
