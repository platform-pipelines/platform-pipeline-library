# githubUpsertComment

Edits the previous bot comment carrying the same marker instead of adding
another. Without this, a busy PR collects twenty identical scan reports.

## Signature

```groovy
def call(String marker, String markdown)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `marker` | `String` | Hidden marker text identifying the bot comment. |
| `markdown` | `String` | Comment body, appended after the marker. |

## Returns

Nothing. No-op outside PR builds (no `env.CHANGE_ID`).

## Usage

```groovy
githubUpsertComment('<!-- sonar-report -->', reportMarkdown)
```

Looks up any existing comment via [`githubFindComment`](githubFindComment.md);
`PATCH`es it if found, otherwise `POST`s a new one, both via
[`githubApiRequest`](githubApiRequest.md). This is what `standardPipeline`
calls (via [`postPlanSummary`](../ci-cd/postPlanSummary.md) and
[`postScanSummary`](../ci-cd/postScanSummary.md)) — see [`githubComment`](githubComment.md)
for the accumulate-instead-of-replace alternative.

## Source

[`vars/githubUpsertComment.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/githubUpsertComment.groovy)
