# scanSecrets

Runs Gitleaks across the repo's full history, not just `HEAD` — a secret
committed and later reverted is still a leaked secret.

## Syntax

```groovy
scanSecrets(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; unused, kept so every quality check has the same signature. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `quality.secretScan` | `true` | `false` | Turns the check on (read by [qualityChecks](qualityChecks.md)). |

Uses `.gitleaks.toml` from the repo root when present.

## Returns

Nothing. Archives `gitleaks-report.json` (secrets redacted) and sets commit
status `ci/secrets`. Fails the build with
`Gitleaks found potential secrets. Rotate anything real, then allowlist false positives in .gitleaks.toml`.

## Examples

```groovy
scanSecrets(cfg)
```

Runs:

```bash
gitleaks detect --source . --config .gitleaks.toml \
  --report-format json --report-path gitleaks-report.json --redact --exit-code 1 --no-banner
```

(`--config` is only passed when `.gitleaks.toml` exists.)

Allowlisting a false positive — in `.gitleaks.toml`, never by turning the
check off:

```toml
[extend]
useDefault = true

[allowlist]
description = "test fixtures"
paths = ['''tests/fixtures/.*\.pem''']
regexes = ['''EXAMPLE_KEY_[A-Z0-9]{16}''']
```

!!! note "History needs a full clone"
    Gitleaks scans the git history available in the workspace. A shallow
    checkout only has the latest commits.

## How it fits

Called from [qualityChecks](qualityChecks.md) when `cfg.quality.secretScan`
is `true`. Runs via [inToolContainer](../images/inToolContainer.md)
(`zricethezav/gitleaks:latest` off the toolbox).

## Source

[`vars/scanSecrets.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/scanSecrets.groovy)
