# goLint

Go lint: formatting, `go vet`, then golangci-lint — ordered cheapest-first so
a formatting slip fails in seconds instead of after a multi-minute lint run.

!!! note "golangci-lint v2 flag"
    Uses `--output.checkstyle.path`, the v2 replacement for the v1
    `--out-format` flag. `toolbox/verify.sh` asserts this flag exists so an
    incompatible golangci-lint upgrade fails at image build, not mid-pipeline.

## Syntax

```groovy
goLint(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `lint.autoFormat` | `false` | `true` | Runs `gofmt -w .` before checking. |
| `lint.failOnError` | `true` | `false` | `false` = gofmt/golangci-lint problems are reported, not fatal. |

## Returns

Nothing. Writes `golangci-report.xml` (Checkstyle). Fails the build:

| Check | Fails when | Message |
|---|---|---|
| `gofmt -l .` | any file listed and `failOnError` | `Run: gofmt -w .` |
| `go vet ./...` | always on failure | `go vet` output |
| `golangci-lint run` | issues found and `failOnError` | `golangci-lint reported problems` |

## Examples

```yaml
buildTool: go
lint:
  failOnError: true
  autoFormat: false
```

```groovy
goLint(cfg)
```

Runs:

```bash
gofmt -l .
go vet ./...
golangci-lint run --timeout 5m --output.checkstyle.path golangci-report.xml
```

Sample failure:

```
[ERROR] Not gofmt-formatted:
internal/router/table.go
ERROR: Run: gofmt -w .
```

Configure linters with a `.golangci.yml` in the repo root:

```yaml
version: "2"
linters:
  enable: [errcheck, govet, staticcheck, revive]
```

## How it fits

Called by [lintApp](lintApp.md) when `cfg.buildTool == 'go'`. The report is
archived by [archiveLintReports](archiveLintReports.md).

## Source

[`vars/goLint.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/goLint.groovy)
