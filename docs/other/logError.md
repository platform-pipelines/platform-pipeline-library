# logError

Error line. Does not fail the build — call Jenkins's `error()` step for that.

## Signature

```groovy
def call(String msg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `msg` | `String` | The message to log. |

## Returns

Nothing — writes `[ERROR] <msg>` to the build log.

## Usage

```groovy
logError "Trivy scan found critical vulnerabilities"
```

## Source

[`vars/logError.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/logError.groovy)
