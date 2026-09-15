# terraformDriftPipeline

Scheduled drift detection for a Terraform repo: plan every environment against
real state and report differences. Never applies.

## Syntax

```groovy
@Library('platform-pipeline@main') _

terraformDriftPipeline()
terraformDriftPipeline(schedule: 'H 6 * * 1-5')
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `schedule` | `String` | no | `'H 6 * * *'` | Cron spec; `''` disables the trigger. |
| any other key | — | no | — | Inline config override (see [configLoad](../other/configLoad.md)). |

## Returns

Nothing.

| Outcome | Build result | Slack |
|---|---|---|
| every plan empty | SUCCESS | — |
| any environment has changes | UNSTABLE, lists `prod: 1 to update` | always, regardless of `notify.on` |
| a plan or policy check fails | FAILURE | per `notify.on` |

## Examples

```groovy
// Jenkinsfile.drift — Pipeline job, branch main
@Library('platform-pipeline@main') _
terraformDriftPipeline(schedule: 'H 6 * * 1-5')
```

Every declared environment is planned with [terraformPlan](terraformPlan.md) —
the same credentials, backend, var files and policy checks as a deploy —
regardless of `branchPattern`. Only `buildTool: terraform` is supported.

## Source

[`vars/terraformDriftPipeline.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/terraformDriftPipeline.groovy)
