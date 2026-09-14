# logWarn

Warning line. Does not affect the build result on its own.

## Syntax

```groovy
logWarn 'message'
logWarn(String msg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `msg` | `String` | yes | — | The message to log. |

## Returns

Nothing — writes `[WARN]  <msg>` to the build log. To also mark the build
yellow, call Jenkins's `unstable('reason')` step.

## Examples

```groovy
logWarn "GitHub call failed: ${what}"
```

Warn and mark the build unstable:

```groovy
if (!fileExists('coverage.xml')) {
    logWarn 'No coverage.xml — coverage gate skipped'
    unstable 'coverage report missing'
}
```

Output:

```
[WARN]  No coverage.xml — coverage gate skipped
```

## Source

[`vars/logWarn.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/logWarn.groovy)
