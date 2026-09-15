# withRegistryAuth

Runs a body with registry credentials exported as `DOCKER_CONFIG`, and deletes
them afterwards even when the body fails. kaniko, buildah, trivy, cosign and
crane all read `DOCKER_CONFIG`.

## Syntax

```groovy
withRegistryAuth(Map cfg) { … }
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; `imageRepo` picks the registry. |
| `body` | `Closure` | yes | — | Steps that pull or push. |

## Returns

Whatever the body returns.

## Examples

```groovy
withRegistryAuth(cfg) {
    buildImage(cfg)
    generateSbom(cfg)
    signImage(cfg)
}
```

| Registry | Credentials |
|---|---|
| `ghcr.io/…`, Harbor, Docker Hub | `REGISTRY_CREDENTIALS_ID` (default `ghcr-credentials`) |
| `<account>.dkr.ecr.<region>.amazonaws.com/…` | `aws ecr get-login-password` under [withAwsCredentials](../cloud/withAwsCredentials.md) — no registry credential |

The config file lives in `<workspace>@tmp/registry-auth`, never in the build
context. See [kanikoDockerConfig](kanikoDockerConfig.md).

## Source

[`vars/withRegistryAuth.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/withRegistryAuth.groovy)
