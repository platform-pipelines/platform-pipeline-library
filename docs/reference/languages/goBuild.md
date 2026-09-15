# goBuild

Compile check for a Go module: downloads modules and builds every package.
The stripped, version-stamped release binary is produced later by
[goPackage](goPackage.md).

## Syntax

```groovy
goBuild(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; unused, kept so every `*Build` step has the same signature. |

## Returns

Nothing. Fails the build if a module can't be downloaded or any package fails
to compile.

## Examples

```yaml
# .ci/config.yaml — from examples/go-service
appName: edge-router
buildTool: go
runtimeVersion: "1.27"
```

```groovy
goBuild(cfg)
```

Runs:

```bash
go mod download
go build ./...
```

Off the toolbox, [inBuildContainer](../images/inBuildContainer.md) runs it in
`golang:1.27` (from `runtimeVersion`).

## How it fits

Called by [buildApp](../pipelines/buildApp.md) when `cfg.buildTool == 'go'`.

## Source

[`vars/goBuild.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/goBuild.groovy)
