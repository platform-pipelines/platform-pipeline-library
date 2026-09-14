# githubApiUrl

Overridable for GitHub Enterprise.

## Signature

```groovy
def call()
```

## Returns

`env.GITHUB_API_URL` if set, else the public GitHub API base URL
(`https://api.github.com`).

## Usage

```groovy
def url = githubApiUrl()
```

Used by every step that talks to the GitHub API, notably
[`githubApiRequest`](githubApiRequest.md) and [`githubFetchFile`](githubFetchFile.md).

## Source

[`vars/githubApiUrl.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/githubApiUrl.groovy)
