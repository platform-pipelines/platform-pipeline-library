# scanTrivy

Trivy scan of a filesystem (dependencies in lock files) or a container image.

## Syntax

```groovy
scanTrivy(cfg: cfg)                                          // filesystem scan of .
scanTrivy(cfg: cfg, target: '<path>', type: 'fs')
scanTrivy(cfg: cfg, target: '<image ref>', type: 'image')
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |
| `target` | `String` | no | `.` | Path (`fs`) or image reference (`image`). |
| `type` | `String` | no | `fs` | `fs` or `image`. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `quality.trivyFailOn` | `[HIGH, CRITICAL]` | `[CRITICAL]` | Severities that fail the build. |
| `quality.trivyIgnoreUnfixed` | `true` | `false` | `true` = CVEs with no available fix don't fail the build. |

Uses `.trivyignore` from the repo root when present.

## Returns

Nothing. Writes and archives `trivy-<type>.json` and `trivy-<type>.txt`, logs
a one-line tally, and sets commit status `ci/trivy-<type>`. Fails the build
with `Trivy found HIGH,CRITICAL vulnerabilities in <target>. See trivy-<type>.txt`.

!!! note
    Reports are written before the pass/fail scan runs, so a failing scan
    still leaves something readable behind rather than only an exit code.
    The report files always include every severity; only the final check is
    filtered.

## Examples

**Filesystem scan (Quality & Security stage):**

```yaml
quality:
  trivy: true
  trivyFailOn: [HIGH, CRITICAL]
  trivyIgnoreUnfixed: true
```

```groovy
scanTrivy(cfg: cfg, target: '.', type: 'fs')
```

Runs:

```bash
trivy fs --format json  --output trivy-fs.json  --no-progress . || true
trivy fs --format table --output trivy-fs.txt   --no-progress . || true
trivy fs --severity HIGH,CRITICAL --ignore-unfixed --exit-code 1 --no-progress --quiet .
```

Log output:

```
[INFO]  Findings: HIGH=2, MEDIUM=5
```

**Image scan (Scan Image stage):**

```groovy
scanTrivy(cfg: cfg, target: "${cfg.imageRepo}:${env.IMAGE_TAG}", type: 'image')
// target → ghcr.io/acme/orders-api:1.4.0, reports → trivy-image.json / trivy-image.txt
```

**Only fail on critical:**

```yaml
quality:
  trivyFailOn: [CRITICAL]
```

**Accepting a specific CVE** — `.trivyignore`:

```
# urllib3 — not reachable in our usage; revisit 2026-12-01
CVE-2025-50181
```

## How it fits

The filesystem scan is called from [qualityChecks](qualityChecks.md) when
`cfg.quality.trivy` is `true` and the repo isn't infra (infra repos use
[scanIac](scanIac.md)); the image scan is called by
[standardPipeline](standardPipeline.md) after the image is pushed. The tally
comes from [trivySummary](trivySummary.md).

## Source

[`vars/scanTrivy.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/scanTrivy.groovy)
