# imageDigest

Resolves an image tag to its registry digest and fails when the tag does not
exist, so a CD run never commits a tag nobody pushed.

## Syntax

```groovy
String imageDigest(Map cfg, String tag)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; reads `imageRepo`. |
| `tag` | `String` | yes | — | Tag to resolve. |

## Returns

The digest (`sha256:…`), also set as `env.IMAGE_DIGEST`. Errors with
`Image ghcr.io/acme/api:9.9.9 was not found in the registry: …` otherwise.

## Examples

```groovy
withRegistryAuth(cfg) {
    imageDigest(cfg, env.IMAGE_TAG)
}
```

Runs `crane digest` in `gcr.io/go-containerregistry/crane:debug` on every
agent (the toolbox does not carry crane), authenticated through
`DOCKER_CONFIG` from [withRegistryAuth](withRegistryAuth.md).

## Source

[`vars/imageDigest.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/imageDigest.groovy)
