# logError

Error line. Does not fail the build — call Jenkins's `error()` step for that.

## Syntax

```groovy
logError 'message'
logError(String msg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `msg` | `String` | yes | — | The message to log. |

## Returns

Nothing — writes `[ERROR] <msg>` to the build log. The build keeps running.

## Examples

```groovy
logError 'Trivy scan found critical vulnerabilities'
```

Log the detail, then fail with a short reason:

```groovy
def unformatted = sh(script: 'gofmt -l .', returnStdout: true).trim()
if (unformatted) {
    logError "Not gofmt-formatted:\n${unformatted}"
    error 'Run: gofmt -w .'
}
```

Output:

```
[ERROR] Not gofmt-formatted:
internal/api/handler.go
cmd/server/main.go
```

## Source

[`vars/logError.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/logError.groovy)
