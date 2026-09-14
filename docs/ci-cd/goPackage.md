# goPackage

Builds a stripped, statically-linked release binary for a Go service, with the
version stamped into `main.version`.

## Syntax

```groovy
goPackage(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; `cfg.appName` names the binary. |

Reads `env.APP_VERSION`.

## Returns

Nothing. Writes `dist/<appName>`, which [appArtifacts](appArtifacts.md)
(`dist/*`) archives.

## Examples

```yaml
appName: edge-router
buildTool: go
```

```groovy
goPackage(cfg)
```

Runs (with `APP_VERSION=1.4.0`):

```bash
CGO_ENABLED=0 go build -ldflags '-s -w -X main.version=1.4.0' -o dist/edge-router ./...
```

To receive the version, declare the variable in `package main`:

```go
package main

var version = "dev"   // overwritten at build time

func main() {
    log.Printf("edge-router %s starting", version)
}
```

!!! note "One main package"
    `-o dist/<appName>` with `./...` expects the module to build a single
    `main` package. Repos with several commands should build them in their
    Dockerfile instead.

## How it fits

Called by [packageApp](packageApp.md) when `cfg.buildTool == 'go'`.

## Source

[`vars/goPackage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/goPackage.groovy)
