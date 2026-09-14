# configImageBuilders

Single source of truth for legal `imageBuilder` values.

## Signature

```groovy
def call()
```

## Returns

`List` — `['kaniko-docker', 'kaniko-k8s', 'buildah']`.

## Usage

```groovy
def builders = configImageBuilders()
```

Read by [`configValidate`](configValidate.md) and by
[buildImage](../ci-cd/buildImage.md)'s error message. Adding a builder means
adding it here, writing its `buildImage*` step, and adding a case to
`buildImage`.

## Source

[`vars/configImageBuilders.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configImageBuilders.groovy)
