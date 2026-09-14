# slackShouldNotify

Notification policy.

!!! note "Why 'change' is the default"
    A channel that goes green every twenty minutes trains people to ignore
    it, including when it goes red.

## Signature

```groovy
def call(String policy, String status)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `policy` | `String` | `'always'`, `'failure'`, or `'change'`. |
| `status` | `String` | The current build result. |

## Returns

`true` if a notification should be sent for this policy/status combination.

## Usage

```groovy
if (slackShouldNotify(cfg.notify.on, currentBuild.currentResult)) { ... }
```

- `always` — every build.
- `failure` — only `FAILURE`/`UNSTABLE`.
- `change` — only when the result differs from the previous build's.

Called by [`notifySlack`](notifySlack.md).

## Source

[`vars/slackShouldNotify.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/slackShouldNotify.groovy)
