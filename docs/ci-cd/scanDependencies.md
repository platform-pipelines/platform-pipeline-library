# scanDependencies

Runs OWASP Dependency-Check. Opt-in: it's slow and needs a warm NVD cache to
be tolerable, so most repos rely on [scanTrivy](scanTrivy.md) instead.

## Syntax

```groovy
scanDependencies(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `quality.dependencyCheck` | `false` | `true` | Turns the check on (read by [qualityChecks](qualityChecks.md)). |
| `quality.dependencyCheckCvss` | `7` | `8.5` | Fail on any CVE with CVSS ≥ this, 0–10. |
| `appName` | — | `orders-api` | Project name in the report. |

## Returns

Nothing. Archives `dependency-check-report/*` (HTML, JSON, XML, …) and sets
commit status `ci/dependency-check`. Fails the build with
`Dependency-Check found vulnerabilities at CVSS >= 7` when the threshold is
met.

## Examples

```yaml
# .ci/config.yaml — from examples/python-service
appName: orders-api
quality:
  dependencyCheck: true
  dependencyCheckCvss: 7
```

```groovy
scanDependencies(cfg)
```

Runs, in `owasp/dependency-check:latest` with the NVD data on the
`dc-nvd-cache` volume:

```bash
/usr/share/dependency-check/bin/dependency-check.sh \
  --project "orders-api" \
  --scan . \
  --format ALL \
  --out dependency-check-report \
  --failOnCVSS 7 \
  --disableAssembly
```

The step passes no `--suppression` file, so a false positive has to be
resolved by upgrading or replacing the dependency, or by raising
`quality.dependencyCheckCvss`.

!!! note "Docker required"
    This step always uses `docker.image(...)`, even on toolbox agents
    (Dependency-Check isn't in the toolbox image), so the agent needs a
    docker socket.

## How it fits

Called from [qualityChecks](qualityChecks.md) when
`cfg.quality.dependencyCheck` is `true`.

## Source

[`vars/scanDependencies.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/scanDependencies.groovy)
