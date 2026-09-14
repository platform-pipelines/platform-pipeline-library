# githubFileSha

Blob sha of a file in a repo, or empty string if it does not exist yet.

## Syntax

```groovy
githubFileSha(String slug, String branch, String path)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `slug` | `String` | yes | — | `owner/name` of the repo; anything else is rejected before reaching the shell. |
| `branch` | `String` | yes | — | Branch or ref to look in. |
| `path` | `String` | yes | — | File path within the repo. |

## Returns

The blob sha as a `String` (40 hex characters), or `''` if the file does not
exist. Fails the build if `slug` is malformed.

## Examples

```groovy
githubFileSha('acme/gitops-manifests', 'main', 'apps/orders-api/prod/kustomization.yaml')
// → '3f1c9e0b7a2d4c8e9f0a1b2c3d4e5f60718293a4'

githubFileSha('acme/gitops-manifests', 'main', 'apps/new-service/dev/kustomization.yaml')
// → ''   (file not created yet)
```

Create-or-update decision, as [`githubCommitFile`](githubCommitFile.md) does it:

```groovy
def payload = [message: 'chore: update values', content: encoded, branch: 'main']
def sha = githubFileSha('acme/gitops-manifests', 'main', 'apps/orders-api/values.yaml')
if (sha) { payload.sha = sha }   // replace; omit for create
```

## How it fits

Parses the contents API response via the bundled `github_file_sha.py`
script (see [`useScript`](useScript.md)). Used by
[`githubCommitFile`](githubCommitFile.md).

## Source

[`vars/githubFileSha.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/githubFileSha.groovy)
