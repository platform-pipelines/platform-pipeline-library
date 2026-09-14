# logActor

Who triggered this build.

!!! note "Why not BUILD_USER"
    Walks the build causes chain rather than reading `BUILD_USER`, which
    only exists if you install the build-user-vars plugin — deliberately not
    in `plugins.txt`.

## Syntax

```groovy
logActor()
```

## Parameters

None. Reads `currentBuild.getBuildCauses()`.

## Returns

A `String`, checked in this order:

| Build was started by | Result |
|---|---|
| a person clicking **Build** / **Replay** | their user id, e.g. `jane.doe` |
| a push / SCM poll | `scm-trigger` |
| a cron trigger | `timer` |
| multibranch branch indexing | `branch-indexing` |
| anything else | the first cause's short description, e.g. `Started by upstream project "orders-api/main"` |
| no causes / an exception | `unknown` |

## Examples

```groovy
def who = logActor()                     // → 'jane.doe'
logInfo "Triggered by ${logActor()}"     // → [INFO]  Triggered by scm-trigger
```

Blocking manual runs of a job:

```groovy
if (logActor() !in ['scm-trigger', 'branch-indexing']) {
    error 'This job only runs from a push'
}
```

## How it fits

Used by [`logAudit`](logAudit.md) and [`slackPayload`](slackPayload.md) to
attribute a build, and by [approvalGate](../ci-cd/approvalGate.md) to block
self-approval.

## Source

[`vars/logActor.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/logActor.groovy)
