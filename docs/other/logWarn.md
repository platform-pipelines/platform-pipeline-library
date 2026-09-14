# logWarn

Warning line. Does not affect build result on its own.

## Signature

```groovy
def call(String msg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `msg` | `String` | The message to log. |

## Returns

Nothing — writes `[WARN]  <msg>` to the build log.

## Usage

```groovy
logWarn "GitHub call failed: ${what}"
```

## Source

[`vars/logWarn.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/logWarn.groovy)
