# githubApiUrl

Base URL for GitHub API calls. Overridable for GitHub Enterprise.

## Syntax

```groovy
githubApiUrl()
```

## Parameters

None. Reads the environment variable below.

| Environment variable | Required | Default | Sample value |
|---|---|---|---|
| `GITHUB_API_URL` | no | `https://api.github.com` | `https://github.acme.internal/api/v3` |

## Returns

`env.GITHUB_API_URL` if set, else `'https://api.github.com'`. No trailing slash.

## Examples

```groovy
githubApiUrl()                               // → 'https://api.github.com'

withEnv(['GITHUB_API_URL=https://github.acme.internal/api/v3']) {
    githubApiUrl()                           // → 'https://github.acme.internal/api/v3'
}

def url = "${githubApiUrl()}/repos/acme/orders-api"
```

For a whole controller, set it once in JCasC:

```yaml
jenkins:
  globalNodeProperties:
    - envVars:
        env:
          - key: GITHUB_API_URL
            value: https://github.acme.internal/api/v3
```

## How it fits

Used by every step that talks to the GitHub API, notably
[`githubApiRequest`](githubApiRequest.md), [`githubFetchFile`](githubFetchFile.md),
[`githubFileSha`](githubFileSha.md) and [`githubFindComment`](githubFindComment.md).

## Source

[`vars/githubApiUrl.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/githubApiUrl.groovy)
