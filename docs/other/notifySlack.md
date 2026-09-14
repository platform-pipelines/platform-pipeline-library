# notifySlack

Slack via incoming webhook — no Slack plugin required.

## Syntax

```groovy
notifySlack(Map cfg, String status)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |
| `status` | `String` | yes | — | Build result: `SUCCESS`, `FAILURE`, `UNSTABLE` or `ABORTED`. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `notify.slackChannel` | `null` | `"#orders-ci"` | Unset → no message at all. |
| `notify.on` | `change` | `always`, `failure` | When to send — see [`slackShouldNotify`](slackShouldNotify.md). |
| `appName` | — | `orders-api` | Shown in the message title. |

Credential: `slack-webhook` (Jenkins string credential holding the incoming
webhook URL).

## Returns

Nothing. Posts one message, or does nothing if `notify.slackChannel` is unset
or the policy says this result isn't worth a message. A failed `curl` is
ignored — notifications never fail the build.

## Examples

```yaml
# .ci/config.yaml
notify:
  slackChannel: "#orders-ci"
  on: failure
```

```groovy
notifySlack(cfg, currentBuild.currentResult)   // FAILURE → message sent
notifySlack(cfg, 'SUCCESS')                    // on: failure → skipped
```

In a custom declarative pipeline:

```groovy
post {
    failure { script { notifySlack(cfg, 'FAILURE') } }
    fixed   { script { notifySlack(cfg, 'SUCCESS') } }
}
```

The message looks like:

```
✅ orders-api — SUCCESS                (links to the build)
Version  1.4.0          Branch        main
Duration 6 min 12 sec   Triggered by  jane.doe
Approved by sam.lee
Jenkins · build #42
```

## How it fits

Builds the body with [`slackPayload`](slackPayload.md), writes it to
`.slack-payload.json`, POSTs it with `curl`, then deletes the file. Called
from [standardPipeline](../ci-cd/standardPipeline.md)'s `post` block.

## Source

[`vars/notifySlack.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/notifySlack.groovy)
