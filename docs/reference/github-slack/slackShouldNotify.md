# slackShouldNotify

Notification policy: is this build result worth a Slack message?

!!! note "Why 'change' is the default"
    A channel that goes green every twenty minutes trains people to ignore
    it, including when it goes red.

## Syntax

```groovy
slackShouldNotify(String policy, String status)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `policy` | `String` | yes | — | `always`, `failure` or `change` — normally `cfg.notify.on`. An unknown value behaves like `always`. |
| `status` | `String` | yes | — | The current build result, normally `currentBuild.currentResult`. |

## Returns

`true` if a message should be sent:

| Policy | Sends when | Example: previous `SUCCESS`, current `SUCCESS` | previous `SUCCESS`, current `FAILURE` | previous `FAILURE`, current `FAILURE` |
|---|---|---|---|---|
| `always` | every build | `true` | `true` | `true` |
| `failure` | result is `FAILURE` or `UNSTABLE` | `false` | `true` | `true` |
| `change` | result differs from the previous build | `false` | `true` | `false` |

## Examples

```groovy
slackShouldNotify('always', 'SUCCESS')      // → true
slackShouldNotify('failure', 'UNSTABLE')    // → true
slackShouldNotify('failure', 'SUCCESS')     // → false
slackShouldNotify('change', 'SUCCESS')      // → true only if the previous build wasn't SUCCESS
```

```groovy
if (slackShouldNotify(cfg.notify.on, currentBuild.currentResult)) {
    // send something custom
}
```

```yaml
# .ci/config.yaml
notify:
  slackChannel: "#orders-ci"
  on: change
```

## How it fits

Called by [`notifySlack`](notifySlack.md).

## Source

[`vars/slackShouldNotify.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/slackShouldNotify.groovy)
