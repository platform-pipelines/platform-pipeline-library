# publishArtifact

Attaches build output to a GitHub release of the repo being built, tagged
`v<version>`. Artifacts live next to the source, and images next to them in
GHCR, with no separate artifact store to run or credential.

## Syntax

```groovy
publishArtifact(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

Reads `env.APP_VERSION`, `env.BRANCH_NAME` and the repo from the checkout
(via [githubRepoSlug](../github-slack/githubRepoSlug.md)).

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `publish.githubRelease` | `false` | `true` | Upload artifacts as release assets. Off → nothing is published. |
| `publish.branchPattern` | `main` | `release/*` | Only builds of matching branches publish (`*` is the only wildcard), so feature branches do not create a release per build. |
| `buildTool` | — | `python` | Which files to upload, via [appArtifacts](../pipelines/appArtifacts.md). |

### Controller requirements

| Item | Kind | Sample value |
|---|---|---|
| `github-token` (or `GITHUB_CREDENTIALS_ID`) | string credential | a token with `contents: write` on the repo |
| `GITHUB_API_URL` | environment variable, optional | `https://github.acme.internal/api/v3` for Enterprise |

## Returns

Nothing. Finds or creates the release with
[githubRelease](../github-slack/githubRelease.md) and uploads every file matching
the artifact glob with
[githubUploadReleaseAsset](../github-slack/githubUploadReleaseAsset.md). A version with a pre-release suffix (`1.4.0-rc.42`)
creates a pre-release. An asset that already exists under the same name is
replaced, so rebuilding a version works. Skips (with a debug or warning line)
when `publish.githubRelease` is off, the branch does not match, the tool has
no artifact glob, or no files match. Fails the build if an upload fails.

## Examples

```yaml
# .ci/config.yaml — from examples/python-service
appName: orders-api
buildTool: python
publish:
  githubRelease: true
```

```groovy
packageApp(cfg)          // writes dist/orders_api-1.4.0-py3-none-any.whl, dist/orders_api-1.4.0.tar.gz
publishArtifact(cfg)
```

Result on `main`:

```
https://github.com/acme/orders-api/releases/tag/v1.4.0
  orders_api-1.4.0-py3-none-any.whl
  orders_api-1.4.0.tar.gz
```

Log and audit output:

```
====================================================================
  Publish to GitHub release v1.4.0 (acme/orders-api)
====================================================================
[INFO]  Published orders_api-1.4.0-py3-none-any.whl
[INFO]  Published orders_api-1.4.0.tar.gz
[AUDIT] artifact.publish [release:v1.4.0, url:https://github.com/acme/orders-api/releases/tag/v1.4.0, count:2]
```

!!! note "Release assets, not package registries"
    Files are uploaded as plain release assets. To install a wheel, jar or
    tarball as a dependency, download it from the release. Container images
    are pushed separately to GHCR by [buildImage](../images/buildImage.md).

## How it fits

Called from the `Package` stage of [standardPipeline](../pipelines/standardPipeline.md)
for non-infra repos, after [packageApp](../pipelines/packageApp.md).

## Source

[`vars/publishArtifact.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/publishArtifact.groovy)
