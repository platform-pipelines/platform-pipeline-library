# slackPayload

Builds the Slack message body: a channel plus one coloured attachment.

## Syntax

```groovy
slackPayload(Map cfg, String status)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; reads `appName` and `notify.slackChannel`. |
| `status` | `String` | yes | — | `SUCCESS`, `FAILURE`, `UNSTABLE` or `ABORTED`. Any other value gets grey and no icon. |

Also reads `env.APP_VERSION`, `env.BRANCH_NAME`, `env.BUILD_URL`,
`env.BUILD_NUMBER`, `env.DEPLOY_APPROVER` and `currentBuild.durationString`.

| Status | Colour | Icon |
|---|---|---|
| `SUCCESS` | `#2eb886` | `:white_check_mark:` |
| `FAILURE` | `#cc0000` | `:x:` |
| `UNSTABLE` | `#e8a317` | `:warning:` |
| `ABORTED` | `#808080` | `:black_square_for_stop:` |

## Returns

A `Map` ready to serialise with `JsonOutput.toJson`. The `Approved by` field
only appears when `env.DEPLOY_APPROVER` is set.

## Examples

```groovy
def payload = slackPayload(cfg, 'SUCCESS')
writeFile file: 'payload.json', text: groovy.json.JsonOutput.prettyPrint(groovy.json.JsonOutput.toJson(payload))
```

Result (with the sample build environment and `DEPLOY_APPROVER=sam.lee`):

```json
{
  "channel": "#orders-ci",
  "attachments": [{
    "color": "#2eb886",
    "fallback": "orders-api: SUCCESS",
    "title": ":white_check_mark: orders-api — SUCCESS",
    "title_link": "https://jenkins.acme.internal/job/orders-api/job/main/42/",
    "fields": [
      {"title": "Version",      "value": "1.4.0",          "short": true},
      {"title": "Branch",       "value": "main",           "short": true},
      {"title": "Duration",     "value": "6 min 12 sec",   "short": true},
      {"title": "Triggered by", "value": "jane.doe",       "short": true},
      {"title": "Approved by",  "value": "sam.lee",        "short": true}
    ],
    "footer": "Jenkins · build #42"
  }]
}
```

## How it fits

Called by [`notifySlack`](notifySlack.md). Uses [`logActor`](logActor.md) for
"Triggered by".

## Source

[`vars/slackPayload.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/slackPayload.groovy)
