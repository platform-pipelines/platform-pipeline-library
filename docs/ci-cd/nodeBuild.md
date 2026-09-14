# nodeBuild

Installs dependencies and runs the project's build script, if it has one.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config, passed through to [nodeInstall](nodeInstall.md). |

## Returns

Nothing — runs `npm run build --if-present` after installing dependencies.

## Usage

```groovy
nodeBuild(cfg)
```

Called by [buildApp](buildApp.md) when `cfg.buildTool == 'npm'`.

## Source

[`vars/nodeBuild.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/nodeBuild.groovy)
