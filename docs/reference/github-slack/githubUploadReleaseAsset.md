# githubUploadReleaseAsset

Uploads one file as an asset on a GitHub release. Asset names are unique per
release, so an asset already there under the same name is deleted first —
rebuilding a version replaces its files instead of failing.

!!! note "Hardened against shell injection"
    The file path and the upload URL are each shell-quoted as a single
    argument to `curl`, and the asset name is URL-encoded.

## Syntax

```groovy
githubUploadReleaseAsset(
    release: <Map from githubRelease>,
    path   : '<file to upload>',
    name   : '<asset name>'        // [optional]
)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `release` | `Map` | yes | — | The release [githubRelease](githubRelease.md) returns; `upload_url` and `assets` are read. |
| `path` | `String` | yes | — | File to upload, relative to the workspace. |
| `name` | `String` | no | last segment of `path` | Asset name shown on the release. |

Uses the [githubCredentialsId](githubCredentialsId.md) token, which needs
`contents: write`.

## Returns

Nothing. Fails the build when `release` has no `upload_url`, or when deleting
the old asset or uploading the new one fails.

## Examples

```groovy
def release = githubRelease(repo: 'acme/edge-router', tag: 'v1.4.0')
githubUploadReleaseAsset(release: release, path: 'dist/edge-router')
// → asset 'edge-router' on https://github.com/acme/edge-router/releases/tag/v1.4.0
```

Publishing a file under a platform-specific name:

```groovy
githubUploadReleaseAsset(release: release, path: 'dist/edge-router', name: 'edge-router-linux-amd64')
```

## How it fits

Called once per artifact by [publishArtifact](../deploy/publishArtifact.md),
after [githubRelease](githubRelease.md) finds or creates the release.

## Source

[`vars/githubUploadReleaseAsset.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/githubUploadReleaseAsset.groovy)
