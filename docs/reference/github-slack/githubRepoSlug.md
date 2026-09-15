# githubRepoSlug

`owner/repo` derived from the checkout rather than configured a second time
in `config.yaml`, where it would eventually drift.

## Syntax

```groovy
githubRepoSlug()
```

## Parameters

None. Reads `env.GIT_URL`, falling back to `git config --get remote.origin.url`.

## Returns

`'owner/repo'`. Works for HTTPS and SSH remotes, with or without `.git`.
Fails the build if the remote is not a `github.com` URL:
`Cannot derive repo slug from remote: <url>`.

## Examples

| Remote URL | Result |
|---|---|
| `https://github.com/acme/orders-api.git` | `acme/orders-api` |
| `https://github.com/acme/orders-api` | `acme/orders-api` |
| `git@github.com:acme/orders-api.git` | `acme/orders-api` |
| `https://gitlab.com/acme/orders-api.git` | fails the build |

```groovy
def slug = githubRepoSlug()                                // → 'acme/orders-api'
def link = "https://github.com/${githubRepoSlug()}/commit/${env.GIT_COMMIT}"
```

!!! note "GitHub Enterprise"
    The pattern matches `github.com` in the remote URL. On an Enterprise host
    whose name doesn't contain `github.com`, this step fails — override
    `GIT_URL` or wrap the call accordingly.

## How it fits

Used by steps that act on the current repo rather than a caller-supplied one:
[`githubSetStatus`](githubSetStatus.md), [`githubComment`](githubComment.md),
[`githubUpsertComment`](githubUpsertComment.md), [`githubFindComment`](githubFindComment.md),
and [imageLabels](../images/imageLabels.md).

## Source

[`vars/githubRepoSlug.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/githubRepoSlug.groovy)
