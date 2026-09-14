# appCacheDir

Directory worth persisting between builds, or `null`.

## Syntax

```groovy
appCacheDir(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; only `cfg.buildTool` is read. |

## Returns

A directory name `String` for `cfg.buildTool`, or `null`:

| `buildTool` | Cache dir |
|---|---|
| `go` | `.gocache` |
| `python` | `.pip-cache` |
| `maven` | `.m2` |
| `gradle` | `.gradle` |
| `npm` | `node_modules` |
| `terraform` | `.terraform` |
| `cloudformation`, `docker-only` | `null` |

## Examples

```groovy
appCacheDir([buildTool: 'maven'])            // → '.m2'
appCacheDir([buildTool: 'cloudformation'])   // → null
```

```groovy
inContainer(appToolImage(cfg), appCacheDir(cfg)) {
    sh 'mvn -B verify'
}
```

## How it fits

Used by [inBuildContainer](inBuildContainer.md): a non-null value makes
[inContainer](inContainer.md) mount a named cache volume at `/cache`.

## Source

[`vars/appCacheDir.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/appCacheDir.groovy)
