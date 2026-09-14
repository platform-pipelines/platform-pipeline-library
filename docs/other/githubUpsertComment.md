# githubUpsertComment

Edits the previous bot comment carrying the same marker instead of adding
another. Without this, a busy PR collects twenty identical scan reports.

## Syntax

```groovy
githubUpsertComment(String marker, String markdown)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `marker` | `String` | yes | — | Hidden text identifying *this kind* of comment. Use an HTML comment so it doesn't render: `<!-- my-team:perf-report -->`. |
| `markdown` | `String` | yes | — | Comment body. Posted as `<marker>\n\n<markdown>`. |

## Returns

Nothing. No-op outside PR builds (no `env.CHANGE_ID`). A failed API call logs
a warning but does not fail the build.

## Examples

```groovy
githubUpsertComment('<!-- sonar-report -->', reportMarkdown)
```

A perf report that stays a single comment across every push to the PR:

```groovy
def p95 = sh(script: './scripts/p95.sh', returnStdout: true).trim()

githubUpsertComment('<!-- orders-api:perf -->', """### Load test
| Metric | Value |
|---|---|
| p95 latency | ${p95} ms |
| Version | `${env.APP_VERSION}` |""")
```

First build on the PR → new comment. Every later build → the same comment is
edited.

Markers the library already uses — don't reuse them:

| Marker | Posted by |
|---|---|
| `<!-- platform-pipeline:scan-summary -->` | [postScanSummary](../ci-cd/postScanSummary.md) |
| `<!-- platform-pipeline:plan:<env> -->` | [postPlanSummary](../ci-cd/postPlanSummary.md) |

## How it fits

Looks up any existing comment via [`githubFindComment`](githubFindComment.md);
`PATCH`es it if found, otherwise `POST`s a new one, both via
[`githubApiRequest`](githubApiRequest.md). See [`githubComment`](githubComment.md)
for the accumulate-instead-of-replace alternative.

## Source

[`vars/githubUpsertComment.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/githubUpsertComment.groovy)
