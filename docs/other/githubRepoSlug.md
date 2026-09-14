# githubRepoSlug

`owner/repo` derived from the checkout rather than configured a second time
in `config.yaml`, where it would eventually drift.

## Signature

```groovy
def call()
```

## Returns

`'owner/repo'` derived from `env.GIT_URL` or the local git remote. Throws if
it can't be derived.

## Usage

```groovy
def slug = githubRepoSlug()
```

Used by any step that needs the current repo's slug rather than a
caller-supplied one — [`githubSetStatus`](githubSetStatus.md),
[`githubUpsertComment`](githubUpsertComment.md), [`githubFindComment`](githubFindComment.md).

## Source

[`vars/githubRepoSlug.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/githubRepoSlug.groovy)
