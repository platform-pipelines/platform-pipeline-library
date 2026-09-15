# logDebug

Debug line. Only emitted when `PIPELINE_DEBUG=true`, so normal logs stay
readable.

## Syntax

```groovy
logDebug 'message'
logDebug(String msg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `msg` | `String` | yes | — | The message to log. |

| Environment variable | Required | Default | Effect |
|---|---|---|---|
| `PIPELINE_DEBUG` | no | unset | `true` prints debug lines; anything else hides them. |

## Returns

Nothing. Writes `[DEBUG] <msg>` when `env.PIPELINE_DEBUG == 'true'`, otherwise
does nothing.

## Examples

```groovy
logDebug "cache dir resolved to ${appCacheDir(cfg)}"
```

Turning debug output on for one run:

```groovy
withEnv(['PIPELINE_DEBUG=true']) {
    standardPipeline()
}
```

Output with debug on:

```
[DEBUG] cache dir resolved to .pip-cache
[DEBUG] Toolbox agent — running in place
[DEBUG] GitHub ok: status ci/jenkins=pending
```

## Source

[`vars/logDebug.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/logDebug.groovy)
