# logInfo

Informational line. Every step's output goes through one of the
[logging](index.md#logging) helpers instead of `println`/`echo` directly, so
log shape stays consistent across the whole library.

## Signature

```groovy
def call(String msg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `msg` | `String` | The message to log. |

## Returns

Nothing — writes `[INFO]  <msg>` to the build log.

## Usage

```groovy
logInfo "app=${cfg.appName} version=${env.APP_VERSION}"
```

## Source

[`vars/logInfo.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/logInfo.groovy)
