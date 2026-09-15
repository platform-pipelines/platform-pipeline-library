# githubCredentialsId

ID of the Jenkins string credential holding a GitHub App installation token or
fine-grained PAT with `contents:write` and `statuses:write`.

## Syntax

```groovy
githubCredentialsId()
```

## Parameters

None. Reads the environment variable below.

| Environment variable | Required | Default | Sample value |
|---|---|---|---|
| `GITHUB_CREDENTIALS_ID` | no | `github-token` | `github-app-orders` |

## Returns

`env.GITHUB_CREDENTIALS_ID` if set, else `'github-token'`.

## Examples

```groovy
githubCredentialsId()      // → 'github-token'

withCredentials([string(credentialsId: githubCredentialsId(), variable: 'GH_TOKEN')]) {
    sh 'curl -sS -H "Authorization: Bearer $GH_TOKEN" https://api.github.com/user'
}
```

Using a different credential for one job:

```groovy
withEnv(['GITHUB_CREDENTIALS_ID=github-app-orders']) {
    standardPipeline()
}
```

## How it fits

Used by every step that authenticates to GitHub:
[`githubApiRequest`](githubApiRequest.md), [`githubFetchFile`](githubFetchFile.md),
[`githubFileSha`](githubFileSha.md), [`githubFindComment`](githubFindComment.md),
[`githubRelease`](githubRelease.md) and
[`githubUploadReleaseAsset`](githubUploadReleaseAsset.md). Release publishing
needs `contents: write`.

## Source

[`vars/githubCredentialsId.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/githubCredentialsId.groovy)
