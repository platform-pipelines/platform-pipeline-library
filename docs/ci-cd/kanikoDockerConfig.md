# kanikoDockerConfig

Writes a Docker config file for Kaniko to authenticate with, since there's
no daemon to run `docker login` against.

## Signature

```groovy
def call()
```

## Returns

Nothing — writes `/kaniko/.docker/config.json` using the
`REGISTRY_CREDENTIALS_ID` credential (default `ghcr-credentials`).

## Usage

```groovy
kanikoDockerConfig()
```

Called by [buildImage](buildImage.md) before dispatching to a Kaniko
implementation.

## Source

[`vars/kanikoDockerConfig.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/kanikoDockerConfig.groovy)
