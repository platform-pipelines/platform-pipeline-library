# postScanSummary

Posts a scan summary to the PR, replacing the previous one rather than
adding another. No-op outside PR builds.

The point is that a developer sees the result without leaving GitHub. A scan
nobody reads is a scan that doesn't change behavior.

## Signature

```groovy
def call(Map cfg, Map findings)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config (unused directly; kept for call-site consistency). |
| `findings` | `Map` | Check name → result string, rendered as a table. |

## Returns

Nothing. No-op outside pull request builds (`env.CHANGE_ID` unset).

## Usage

```groovy
postScanSummary(cfg, [Trivy: 'clean', Secrets: 'none found'])
```

Posts via [githubUpsertComment](../other/githubUpsertComment.md) under a
fixed marker, so repeat builds edit the same comment. Called from
[standardPipeline](standardPipeline.md)'s `Scan Image` stage.

## Source

[`vars/postScanSummary.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/postScanSummary.groovy)
