# githubSetStatus

Publishes a commit status so PR pages show pass/fail inline.

## Signature

```groovy
def call(String context, String state, String description)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `context` | `String` | Status check name shown on the PR. |
| `state` | `String` | `pending` \| `success` \| `failure` \| `error`. |
| `description` | `String` | Short text, truncated to 140 chars. |

## Returns

Nothing. No-op when `env.GIT_COMMIT` is unavailable.

## Usage

```groovy
githubSetStatus('sonar', 'success', 'Quality gate passed')
```

Calls [`githubApiRequest`](githubApiRequest.md) against the commit statuses
API using [`githubRepoSlug`](githubRepoSlug.md).

## Source

[`vars/githubSetStatus.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/githubSetStatus.groovy)
