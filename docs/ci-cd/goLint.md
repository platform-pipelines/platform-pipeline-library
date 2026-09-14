# goLint

Go lint: formatting, `go vet`, then golangci-lint — ordered cheapest-first so
a formatting slip fails in seconds instead of after a multi-minute lint run.

!!! note "golangci-lint v2 flag"
    Uses `--output.checkstyle.path`, the v2 replacement for the v1
    `--out-format` flag. `toolbox/verify.sh` asserts this flag exists so an
    incompatible golangci-lint upgrade fails at image build, not mid-pipeline.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.lint.autoFormat` and `cfg.lint.failOnError`. |

## Returns

Nothing — errors if `gofmt`/golangci-lint report problems and `failOnError`
is true.

## Usage

```groovy
goLint(cfg)
```

Called by [lintApp](lintApp.md) when `cfg.buildTool == 'go'`.

## Source

[`vars/goLint.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/goLint.groovy)
