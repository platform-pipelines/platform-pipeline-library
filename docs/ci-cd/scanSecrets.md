# scanSecrets

Runs Gitleaks across the repo's full history, not just `HEAD` — a secret
committed and later reverted is still a leaked secret.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; unused here, kept for call-signature consistency across quality checks. |

## Returns

Nothing. Archives `gitleaks-report.json` and throws if any secret is
detected.

## Usage

```groovy
scanSecrets(cfg)
```

!!! note
    A false positive is allowlisted in `.gitleaks.toml`, not by disabling the
    check — the error message points there directly.

Called from [qualityChecks](qualityChecks.md) when `cfg.quality.secretScan`
is `true`.

## Source

[`vars/scanSecrets.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/scanSecrets.groovy)
