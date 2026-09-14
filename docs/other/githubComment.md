# githubComment

Adds a PR comment. No-op outside PR builds.

!!! note "Public API, not used internally"
    `standardPipeline` doesn't call this — it calls
    [`githubUpsertComment`](githubUpsertComment.md) instead, because that one
    replaces its own previous comment rather than accumulating. `githubComment`
    is kept as public API for consumers who want a one-off comment from a
    custom stage in their own `Jenkinsfile`.

## Signature

```groovy
def call(String markdown)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `markdown` | `String` | Comment body. |

## Returns

Nothing. No-op outside PR builds (no `env.CHANGE_ID`).

## Usage

```groovy
githubComment('Deployed to staging.')
```

## Source

[`vars/githubComment.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/githubComment.groovy)
