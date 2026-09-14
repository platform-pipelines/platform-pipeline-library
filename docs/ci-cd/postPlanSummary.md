# postPlanSummary

Puts an infrastructure plan summary on the PR so reviewers see the blast
radius without digging through build logs.

## Syntax

```groovy
postPlanSummary(Map cfg, Map envCfg, String summary)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; unused, kept for call-site consistency. |
| `envCfg` | `Map` | yes | — | Target environment; `envCfg.name` is shown and keys the comment. |
| `summary` | `String` | yes | — | One-line summary, normally `env.TF_PLAN_SUMMARY` or `env.CFN_CHANGE_SUMMARY`. |

Reads `env.CHANGE_ID`, `env.APP_VERSION`, `env.GIT_SHORT_SHA`, `env.BUILD_URL`.

## Returns

Nothing. No-op outside pull request builds (`env.CHANGE_ID` unset).

A summary containing `DESTRUCTIVE` or `REPLACEMENT` gets a distinct heading
and warning line — resource destruction is called out, not buried in a
generic "plan ready" comment.

## Examples

**Normal change:**

```groovy
postPlanSummary(cfg, [name: 'dev'], '2 to create, 1 to update')
```

Comment posted on the PR:

```markdown
### Infrastructure plan

**dev**: 2 to create, 1 to update

Version `1.4.0-feature-vpc.42.gab12cd3` · commit `ab12cd3` · [full plan](https://jenkins.acme.internal/job/platform-network/job/PR-87/42/artifact/)
```

**Destructive change:**

```groovy
postPlanSummary(cfg, [name: 'prod'], '1 to update, 1 to replace | DESTRUCTIVE: aws_db_instance.main')
```

```markdown
### Infrastructure plan — DESTRUCTIVE CHANGES

**prod**: 1 to update, 1 to replace | DESTRUCTIVE: aws_db_instance.main

Resources will be destroyed or recreated. Check that state loss is expected before approving.

Version `…` · commit `ab12cd3` · [full plan](…/artifact/)
```

Each environment gets its own comment (marker
`<!-- platform-pipeline:plan:<env> -->`), and re-running the PR build edits
it instead of adding another.

## How it fits

Posts via [githubUpsertComment](../other/githubUpsertComment.md). Called from
[deployTerraform](../cloud/deployTerraform.md) and
[deployCloudFormation](../cloud/deployCloudFormation.md), before the approval
gate.

## Source

[`vars/postPlanSummary.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/postPlanSummary.groovy)
