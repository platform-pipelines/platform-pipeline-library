# publishArtifact

Uploads build output to Nexus over REST, so the same command works from a
laptop as from an agent.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.publish.nexusRepo` and `cfg.appName`. |

## Returns

Nothing. No-op when `publish.nexusRepo` isn't configured (the old
`extra.nexusRepo` still works, with a deprecation warning), the toolchain has nothing
to publish (see [appArtifacts](appArtifacts.md)), or no files match the
artifact glob.

## Usage

```groovy
publishArtifact(cfg)
```

Called from the `Package` stage of [standardPipeline](standardPipeline.md)
for non-infra repos.

## Source

[`vars/publishArtifact.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/publishArtifact.groovy)
