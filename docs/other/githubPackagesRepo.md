# githubPackagesRepo

The GitHub Container Registry path that belongs to the repo being built, so
images and build output live in GitHub Packages next to the code. The path is
derived from the checkout rather than written in `config.yaml`, where it would
eventually drift.

## Syntax

```groovy
githubPackagesRepo()                 // the repo's own path
githubPackagesRepo(String name)      // a package nested under it
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `name` | `String` | no | `null` | Package name nested under the repo path. Letters, digits, `.`, `_`, `-` and `/`. |

Reads the repo through [githubRepoSlug](githubRepoSlug.md).

## Returns

`ghcr.io/<owner>/<repo>` or `ghcr.io/<owner>/<repo>/<name>`, without a tag and
**lowercased** — GHCR rejects upper-case names. Fails the build if `name` is
not a valid package name, or if the remote is not a GitHub URL.

## Examples

| Remote URL | Call | Result |
|---|---|---|
| `https://github.com/acme/orders-api.git` | `githubPackagesRepo()` | `ghcr.io/acme/orders-api` |
| `git@github.com:Acme/Orders-API.git` | `githubPackagesRepo()` | `ghcr.io/acme/orders-api` |
| `https://github.com/acme/orders-api.git` | `githubPackagesRepo('orders-api-artifacts')` | `ghcr.io/acme/orders-api/orders-api-artifacts` |

```yaml
# .ci/config.yaml
imageRegistry: github      # configLoad sets imageRepo = githubPackagesRepo()
publish:
  githubPackages: true     # publishArtifactGithub pushes to githubPackagesRepo('<appName>-artifacts')
```

!!! note "Linking the package to the repo"
    A package pushed from Jenkins (rather than GitHub Actions) is linked to a
    repository through the `org.opencontainers.image.source` label or
    annotation. [imageLabels](../ci-cd/imageLabels.md) sets it on images and
    [publishArtifactGithub](../ci-cd/publishArtifactGithub.md) on artifacts, so
    both show up on the repo's **Packages** tab. New packages are private;
    change visibility in the package settings.

## How it fits

Called by [configLoad](configLoad.md) when `imageRegistry: github` is set and
`imageRepo` is not, and by [publishArtifactGithub](../ci-cd/publishArtifactGithub.md).

## Source

[`vars/githubPackagesRepo.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/githubPackagesRepo.groovy)
