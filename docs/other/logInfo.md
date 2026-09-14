# logInfo

Informational line. Every step's output goes through one of the
[logging](index.md#logging) helpers instead of `println`/`echo` directly, so
log shape stays consistent across the whole library.

## Syntax

```groovy
logInfo 'message'
logInfo(String msg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `msg` | `String` | yes | — | The message to log. |

## Returns

Nothing — writes `[INFO]  <msg>` (two spaces, so it lines up with `[WARN]`)
to the build log.

## Examples

```groovy
logInfo "app=${cfg.appName} version=${env.APP_VERSION}"
logInfo 'Stack already matches the template'
```

Output:

```
[INFO]  app=orders-api version=1.4.0
[INFO]  Stack already matches the template
```

## The logging family

| Step | Prefix | Shown when |
|---|---|---|
| [logDebug](logDebug.md) | `[DEBUG]` | `PIPELINE_DEBUG=true` |
| **logInfo** | `[INFO]` | always |
| [logWarn](logWarn.md) | `[WARN]` | always |
| [logError](logError.md) | `[ERROR]` | always (does not fail the build) |
| [logBanner](logBanner.md) | `====` rules | always |
| [logAudit](logAudit.md) | `[AUDIT]` + `.ci-audit.jsonl` | always |

## Source

[`vars/logInfo.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/logInfo.groovy)
