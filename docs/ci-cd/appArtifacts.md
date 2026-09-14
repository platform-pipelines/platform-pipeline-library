# appArtifacts

Build output worth archiving and publishing, or `null`.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; only `cfg.buildTool` is read. |

## Returns

The artifact glob for `cfg.buildTool` (e.g. `target/*.jar` for `maven`), or
`null` if it produces none.

## Usage

```groovy
def glob = appArtifacts(cfg)
```

Used by [standardPipeline](standardPipeline.md)'s `Package` stage and by
[publishArtifact](publishArtifact.md).

## Source

[`vars/appArtifacts.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/appArtifacts.groovy)
