# notifySlack

Slack via incoming webhook — no Slack plugin required.

## Signature

```groovy
def call(Map cfg, String status)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.notify.slackChannel`/`on`. |
| `status` | `String` | Current build result, checked against notify policy. |

## Returns

Nothing. No-op if `cfg.notify.slackChannel` isn't set, or if
[`slackShouldNotify`](slackShouldNotify.md) says this status shouldn't
trigger a message.

## Usage

```groovy
notifySlack(cfg, currentBuild.currentResult)
```

Builds the message body via [`slackPayload`](slackPayload.md), writes it to
a temp file, and POSTs it to the `slack-webhook` credential with `curl`.

## Source

[`vars/notifySlack.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/notifySlack.groovy)
