# kanikoDockerConfig

Writes a Docker config file with registry credentials, for tools with no daemon
to `docker login` against — kaniko, buildah, trivy, cosign and crane all read
it through `DOCKER_CONFIG`. The registry host comes from `cfg.imageRepo`. Most
callers want [withRegistryAuth](withRegistryAuth.md), which also deletes it.

## Syntax

```groovy
String kanikoDockerConfig(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; `imageRepo` selects the registry; `infra` supplies AWS credentials for ECR. |

| Environment variable | Required | Default | Sample value |
|---|---|---|---|
| `REGISTRY_CREDENTIALS_ID` | no | `ghcr-credentials` | `harbor-robot` |

## Returns

The directory holding `config.json`: `<workspace>@tmp/registry-auth`. It is
outside the workspace on purpose — the workspace is the kaniko build context,
and `COPY . .` would otherwise put the password in the image.

```json
{"auths":{"ghcr.io":{"auth":"<base64 of user:password>"}}}
```

| `imageRepo` | Host | Credentials |
|---|---|---|
| `ghcr.io/acme/api` | `ghcr.io` | `REGISTRY_CREDENTIALS_ID` |
| `123456789012.dkr.ecr.eu-west-1.amazonaws.com/api` | the ECR host | user `AWS`, password from `aws ecr get-login-password --region eu-west-1` under [withAwsCredentials](../infrastructure/withAwsCredentials.md) |
| `registry.local:5000/acme/api` | `registry.local:5000` | `REGISTRY_CREDENTIALS_ID` |
| `acme/api` | `https://index.docker.io/v1/` | `REGISTRY_CREDENTIALS_ID` |

## Examples

```groovy
def dockerConfig = kanikoDockerConfig(cfg)
withEnv(["DOCKER_CONFIG=${dockerConfig}"]) {
    sh "trivy image ${cfg.imageRepo}:${env.IMAGE_TAG}"
}
```

## How it fits

Called by [withRegistryAuth](withRegistryAuth.md), and by
[buildImage](buildImage.md) when no `DOCKER_CONFIG` is set.

## Source

[`vars/kanikoDockerConfig.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/kanikoDockerConfig.groovy)
