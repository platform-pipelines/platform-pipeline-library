# logDebug

Only emitted when `PIPELINE_DEBUG=true`, so normal logs stay readable.

## Signature

```groovy
def call(String msg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `msg` | `String` | The message to log. |

## Returns

Nothing. No-op unless `env.PIPELINE_DEBUG == 'true'`.

## Usage

```groovy
logDebug "cache dir resolved to ${dir}"
```

## Source

[`vars/logDebug.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/logDebug.groovy)
