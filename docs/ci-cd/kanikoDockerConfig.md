# kanikoDockerConfig

Writes a Docker config file for Kaniko to authenticate with, since there's
no daemon to run `docker login` against. The registry host comes from
`cfg.imageRepo`, so ECR, Harbor or a registry with a port get credentials for
the right host.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; `cfg.imageRepo` selects the registry host. |

## Returns

Nothing — writes `/kaniko/.docker/config.json` using the
`REGISTRY_CREDENTIALS_ID` credential (default `ghcr-credentials`).

| `imageRepo` | Registry host written |
|---|---|
| `ghcr.io/acme/api` | `ghcr.io` |
| `registry.local:5000/acme/api` | `registry.local:5000` |
| `acme/api` | `https://index.docker.io/v1/` (Docker Hub) |

## Usage

```groovy
kanikoDockerConfig(cfg)
```

Called by [buildImage](buildImage.md) before dispatching to a Kaniko
implementation.

## Source

[`vars/kanikoDockerConfig.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/kanikoDockerConfig.groovy)
