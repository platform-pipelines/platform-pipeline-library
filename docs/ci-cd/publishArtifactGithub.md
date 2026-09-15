# publishArtifactGithub

Pushes build output to GitHub Packages, in the same repository as the code, as
one OCI artifact in the GitHub Container Registry:

```
ghcr.io/<owner>/<repo>/<appName>-artifacts:<IMAGE_TAG>
```

GitHub Packages has native Maven and npm registries but nothing for Go
binaries, Python wheels, or arbitrary files. [ORAS](https://oras.land) pushes
every toolchain's output the same way, and `oras pull` gets it back.

## Syntax

```groovy
publishArtifactGithub(Map cfg, List files)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; `appName` names the package. |
| `files` | `List` | yes | — | Files to push; each needs `.path` (relative to the workspace) and `.name`, as `findFiles` returns. |

Reads `env.IMAGE_TAG`, `env.APP_VERSION`, `env.GIT_COMMIT` and the repo slug
via [githubPackagesRepo](../other/githubPackagesRepo.md).

### Controller requirements

| Item | Kind | Sample value |
|---|---|---|
| `ghcr-credentials` (or `REGISTRY_CREDENTIALS_ID`) | username/password credential | GitHub user + classic PAT with `write:packages` |

The token must be a **classic** personal access token (or a GitHub App token)
with `write:packages`; fine-grained PATs cannot push to GitHub Packages.

## Returns

The pushed reference, e.g. `ghcr.io/acme/orders-api/orders-api-artifacts:1.4.0`.
Fails the build if `IMAGE_TAG` is unset or the push fails.

The manifest carries the [imageLabels](imageLabels.md) as annotations. The
`org.opencontainers.image.source` annotation is what links the package to the
repository, so it appears on the repo's **Packages** tab beside the image.

## Examples

```yaml
# .ci/config.yaml
appName: orders-api
buildTool: python
publish:
  githubPackages: true
```

```groovy
publishArtifactGithub(cfg, findFiles(glob: appArtifacts(cfg)) as List)
// → 'ghcr.io/acme/orders-api/orders-api-artifacts:1.4.0'
```

Runs (in the toolbox, or `ghcr.io/oras-project/oras:v1.3.4`):

```bash
oras push --registry-config "$DOCKER_CONFIG/config.json" \
  --artifact-type application/vnd.platformpipelines.build-output \
  --annotation 'org.opencontainers.image.source=https://github.com/acme/orders-api' \
  --annotation 'org.opencontainers.image.revision=ab12cd3ef4567890ab12cd3ef4567890ab12cd3e' \
  ... \
  'ghcr.io/acme/orders-api/orders-api-artifacts:1.4.0' \
  'dist/orders_api-1.4.0-py3-none-any.whl' 'dist/orders_api-1.4.0.tar.gz'
```

Fetching the files again, anywhere:

```bash
oras pull ghcr.io/acme/orders-api/orders-api-artifacts:1.4.0
```

!!! note "Credentials when the image lives elsewhere"
    Registry auth is written for `ghcr.io`, whatever `imageRepo` points at,
    so an ECR or Harbor image can still publish artifacts to GitHub. The
    credential ID is `REGISTRY_CREDENTIALS_ID` (default `ghcr-credentials`);
    if you override that for another registry, it must also work for GHCR.

## How it fits

Called by [publishArtifact](publishArtifact.md) when `publish.githubPackages`
is `true`. Uses [withRegistryAuth](withRegistryAuth.md) and
[inToolContainer](inToolContainer.md).

## Source

[`vars/publishArtifactGithub.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/publishArtifactGithub.groovy)
