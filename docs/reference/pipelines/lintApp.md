# lintApp

Routes to the right lint step for this repo's `buildTool`.

## Syntax

```groovy
lintApp(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `lint.enabled` | `true` | `false` | `false` skips linting entirely (`Lint disabled in config`). |
| `buildTool` | — | `npm` | Picks the lint step. |

## Returns

Nothing. Fails the build for an unknown tool:
`No lint step for buildTool 'rust'`.

| `buildTool` | Step | What it runs |
|---|---|---|
| `go` | [goLint](../languages/goLint.md) | `gofmt`, `go vet`, `golangci-lint` |
| `python` | [pythonLint](../languages/pythonLint.md) | `ruff check`, `ruff format --check`, `mypy` |
| `maven` | [mavenLint](../languages/mavenLint.md) | Checkstyle, SpotBugs |
| `gradle` | [gradleLint](../languages/gradleLint.md) | Checkstyle, SpotBugs |
| `npm` | [nodeLint](../languages/nodeLint.md) | ESLint, Prettier, `tsc --noEmit` |
| `terraform` | [terraformLint](../infrastructure/terraformLint.md) | `fmt -check`, `validate`, tflint |
| `cloudformation` | [cfnLint](../infrastructure/cfnLint.md) | `cfn-lint` |
| `docker-only` | [dockerOnlyLint](../languages/dockerOnlyLint.md) | hadolint |

## Examples

```yaml
buildTool: npm
lint:
  enabled: true
  failOnError: false     # report-only while the repo adopts the rules
```

```groovy
lintApp(cfg)             // → nodeLint(cfg)
```

Turning lint off:

```yaml
lint:
  enabled: false
```

## How it fits

Called from the `Lint` stage of [standardPipeline](standardPipeline.md), with
reports archived by [archiveLintReports](../quality/archiveLintReports.md).

## Source

[`vars/lintApp.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/lintApp.groovy)
