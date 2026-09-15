# postScanSummary

Posts a scan summary to the PR, replacing the previous one rather than
adding another. No-op outside PR builds.

The point is that a developer sees the result without leaving GitHub. A scan
nobody reads is a scan that doesn't change behavior.

## Syntax

```groovy
postScanSummary(Map cfg, Map findings)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; unused, kept for call-site consistency. |
| `findings` | `Map` | yes | — | Check name → result text. Rendered as table rows in insertion order. |

Reads `env.CHANGE_ID`, `env.APP_VERSION`, `env.GIT_SHORT_SHA`, `env.BUILD_URL`.

## Returns

Nothing. No-op outside pull request builds (`env.CHANGE_ID` unset).

## Examples

```groovy
postScanSummary(cfg, [
    'Image scan': 'passed',
    'Version'   : env.APP_VERSION,
    'Digest'    : env.IMAGE_DIGEST ?: 'n/a',
])
```

Comment posted (and updated on every later build of the PR):

```markdown
### Pipeline scan summary

| Check | Result |
|---|---|
| Image scan | passed |
| Version | 1.4.0-feature-login.42.gab12cd3 |
| Digest | sha256:7d9c…e41a |

Version `1.4.0-feature-login.42.gab12cd3` · commit `ab12cd3` · [build log](https://jenkins.acme.internal/job/orders-api/job/PR-87/42/)
```

Adding your own rows from a custom stage:

```groovy
postScanSummary(cfg, [
    Trivy   : trivySummary('trivy-image.json'),     // e.g. 'HIGH=2, MEDIUM=5'
    Secrets : 'none found',
    Coverage: "${coveragePercent(cfg)}%",
])
```

!!! note
    There is one summary comment per PR (marker
    `<!-- platform-pipeline:scan-summary -->`), so a later call replaces the
    whole table, including rows posted by `standardPipeline`.

## How it fits

Posts via [githubUpsertComment](../github-slack/githubUpsertComment.md). Called from
[standardPipeline](../pipelines/standardPipeline.md)'s `Scan Image` stage.

## Source

[`vars/postScanSummary.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/postScanSummary.groovy)
