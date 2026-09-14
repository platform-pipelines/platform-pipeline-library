# kanikoDockerConfig

Writes a Docker config file for Kaniko to authenticate with, since there's
no daemon to run `docker login` against. The registry host comes from
`cfg.imageRepo`, so ECR, Harbor or a registry with a port get credentials for
the right host.

## Syntax

```groovy
kanikoDockerConfig(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; `cfg.imageRepo` selects the registry host. |

| Environment variable | Required | Default | Sample value |
|---|---|---|---|
| `REGISTRY_CREDENTIALS_ID` | no | `ghcr-credentials` | `harbor-robot` |

The credential is a Jenkins **username/password** credential (for GHCR: your
GitHub user and a PAT with `write:packages`).

## Returns

Nothing. Writes `/kaniko/.docker/config.json`:

```json
{"auths":{"ghcr.io":{"auth":"<base64 of user:password>"}}}
```

The host is the first path segment of `imageRepo` if it looks like a host
(contains `.` or `:`, or is `localhost`), otherwise Docker Hub:

| `imageRepo` | Registry host written |
|---|---|
| `ghcr.io/acme/api` | `ghcr.io` |
| `123456789012.dkr.ecr.eu-west-1.amazonaws.com/api` | `123456789012.dkr.ecr.eu-west-1.amazonaws.com` |
| `registry.local:5000/acme/api` | `registry.local:5000` |
| `localhost/acme/api` | `localhost` |
| `acme/api` | `https://index.docker.io/v1/` (Docker Hub) |

## Examples

```groovy
kanikoDockerConfig(cfg)       // imageRepo: ghcr.io/acme/orders-api → auth for ghcr.io
```

Using a Harbor robot account:

```groovy
withEnv(['REGISTRY_CREDENTIALS_ID=harbor-robot']) {
    kanikoDockerConfig([imageRepo: 'harbor.acme.internal/orders/orders-api'])
}
```

## How it fits

Called by [buildImage](buildImage.md) before dispatching to any builder.

## Source

[`vars/kanikoDockerConfig.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/kanikoDockerConfig.groovy)
