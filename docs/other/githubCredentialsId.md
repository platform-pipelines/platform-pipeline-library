# githubCredentialsId

String credential holding a GitHub App installation token or fine-grained PAT
with `contents:write` and `statuses:write`.

## Signature

```groovy
def call()
```

## Returns

`env.GITHUB_CREDENTIALS_ID` if set, else `'github-token'`.

## Usage

```groovy
def credId = githubCredentialsId()
```

Used by every step that authenticates to GitHub, via `withCredentials([string(credentialsId: githubCredentialsId(), ...)])`.

## Source

[`vars/githubCredentialsId.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/githubCredentialsId.groovy)
