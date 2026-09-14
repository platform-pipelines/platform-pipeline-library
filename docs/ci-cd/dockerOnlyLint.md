# dockerOnlyLint

Nothing to compile for a `docker-only` repo, but the Dockerfile itself is
worth linting with [hadolint](https://github.com/hadolint/hadolint).

## Syntax

```groovy
dockerOnlyLint(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `dockerfile` | `Dockerfile` | `docker/Dockerfile` | File to lint. |
| `lint.failOnError` | `true` | `false` | `false` = report problems without failing. |

## Returns

Nothing. Fails the build with `hadolint reported problems` when hadolint
finds issues and `lint.failOnError` is `true`.

## Examples

```yaml
buildTool: docker-only
dockerfile: Dockerfile
lint:
  failOnError: true
```

```groovy
dockerOnlyLint(cfg)
```

Runs `hadolint Dockerfile`. Sample output:

```
Dockerfile:3 DL3008 warning: Pin versions in apt get install.
Dockerfile:7 DL3025 warning: Use arguments JSON notation for CMD and ENTRYPOINT arguments
```

Ignoring a rule for the repo with a `.hadolint.yaml`:

```yaml
ignored:
  - DL3008
```

## How it fits

Called by [lintApp](lintApp.md) when `cfg.buildTool == 'docker-only'`.

## Source

[`vars/dockerOnlyLint.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/dockerOnlyLint.groovy)
