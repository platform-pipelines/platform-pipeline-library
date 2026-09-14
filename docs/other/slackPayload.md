# slackPayload

Builds the Slack attachment body.

## Signature

```groovy
def call(Map cfg, String status)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.appName` and `cfg.notify.slackChannel`. |
| `status` | `String` | Build result: `SUCCESS`/`FAILURE`/`UNSTABLE`/`ABORTED`. |

## Returns

`Map` payload ready for the Slack step — a channel plus one colored
attachment with version, branch, duration, who triggered it (via
[`logActor`](logActor.md)), and the approver if `env.DEPLOY_APPROVER` is set.

## Usage

```groovy
def payload = slackPayload(cfg, currentBuild.currentResult)
```

Called by [`notifySlack`](notifySlack.md).

## Source

[`vars/slackPayload.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/slackPayload.groovy)
