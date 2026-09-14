# githubFileSha

Blob sha of a file in a repo, or empty string if it does not exist yet.

## Signature

```groovy
def call(String slug, String branch, String path)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `slug` | `String` | `owner/name` of the repo. |
| `branch` | `String` | Branch or ref to look in. |
| `path` | `String` | File path within the repo. |

## Returns

Blob sha as a `String`, or `''` if the file does not exist.

## Usage

```groovy
def sha = githubFileSha('acme/gitops', 'main', 'apps/api/values.yaml')
```

Parses the contents API response via the bundled `github_file_sha.py`
script (see [`useScript`](useScript.md)). Used by
[`githubCommitFile`](githubCommitFile.md) to decide whether it's creating or
replacing a file.

## Source

[`vars/githubFileSha.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/githubFileSha.groovy)
