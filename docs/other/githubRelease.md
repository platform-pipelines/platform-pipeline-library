# githubRelease

Finds the GitHub release for a tag, creating it when it does not exist yet,
so a rebuild of the same version adds to its release instead of failing.

## Syntax

```groovy
githubRelease(Map args)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `repo` | `String` | yes | — | `owner/name` of the repo. Anything else fails the build. |
| `tag` | `String` | yes | — | Tag the release belongs to (letters, digits, `.`, `_`, `+`, `-`). |
| `commit` | `String` | no | `env.GIT_COMMIT` | Commit a newly created tag points at. Ignored when the tag already exists. |
| `prerelease` | `boolean` | no | `false` | Mark a newly created release as a pre-release. |

Uses [githubApiUrl](githubApiUrl.md) and the
[githubCredentialsId](githubCredentialsId.md) token, which needs
`contents: write`.

## Returns

The release as a `Map` straight from the GitHub API — `id`, `html_url`,
`upload_url`, `assets`, and so on. Fails the build when the release can be
neither read nor created.

## Examples

```groovy
def release = githubRelease(repo: 'acme/orders-api', tag: 'v1.4.0')
release.html_url      // → 'https://github.com/acme/orders-api/releases/tag/v1.4.0'
release.assets*.name  // → ['orders_api-1.4.0.tar.gz'] on a rebuild, [] the first time
```

## How it fits

Called by [publishArtifact](../ci-cd/publishArtifact.md), which uploads the
build output to the returned `upload_url`.

## Source

[`vars/githubRelease.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/githubRelease.groovy)
