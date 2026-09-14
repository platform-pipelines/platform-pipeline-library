# nodeBuild

Installs dependencies and runs the project's `build` script, if it has one.

## Syntax

```groovy
nodeBuild(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; passed to [nodeInstall](nodeInstall.md). |

## Returns

Nothing. Fails the build if install or the build script fails.

## Examples

```yaml
# .ci/config.yaml — from examples/node-service
appName: checkout-api
buildTool: npm
runtimeVersion: "20"
```

```json
{
  "scripts": {
    "build": "tsc -p tsconfig.build.json"
  }
}
```

```groovy
nodeBuild(cfg)
```

Runs:

```bash
npm ci --prefer-offline --no-audit --fund=false
npm run build --if-present
```

With no `build` script, the second command does nothing. Off the toolbox
this runs in `node:20-alpine`.

## How it fits

Called by [buildApp](buildApp.md) when `cfg.buildTool == 'npm'`.

## Source

[`vars/nodeBuild.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/nodeBuild.groovy)
