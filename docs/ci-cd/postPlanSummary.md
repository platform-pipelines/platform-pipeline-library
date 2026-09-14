# postPlanSummary

Puts an infrastructure plan summary on the PR so reviewers see the blast
radius without digging through build logs.

## Signature

```groovy
def call(Map cfg, Map envCfg, String summary)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config (unused directly; kept for call-site consistency). |
| `envCfg` | `Map` | Target environment config; `envCfg.name` is shown in the comment. |
| `summary` | `String` | Plan/change-set summary text. |

## Returns

Nothing. No-op outside pull request builds (`env.CHANGE_ID` unset).

## Usage

```groovy
postPlanSummary(cfg, envCfg, planSummaryText)
```

!!! note
    A summary containing `DESTRUCTIVE` or `REPLACEMENT` gets a distinct
    heading and warning line — resource destruction is called out, not
    buried in a generic "plan ready" comment.

Posts via [githubUpsertComment](../other/githubUpsertComment.md) under a
marker unique to the environment, so re-running a PR build updates the same
comment instead of adding another. Called from
[deployTerraform](../cloud/deployTerraform.md) and
[deployCloudFormation](../cloud/deployCloudFormation.md).

## Source

[`vars/postPlanSummary.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/postPlanSummary.groovy)
