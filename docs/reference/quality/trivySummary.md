# trivySummary

One-line severity tally from a Trivy JSON report, for the audit log.

## Syntax

```groovy
trivySummary(String jsonReport)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `jsonReport` | `String` | yes | — | Path to a Trivy JSON report (`trivy … --format json`). |

## Returns

A `String`:

| Report contents | Result |
|---|---|
| vulnerabilities found | counts in severity order, zeros left out: `CRITICAL=1, HIGH=3, MEDIUM=12` |
| no vulnerabilities | `clean` |
| file can't be parsed | `unreadable` |
| file doesn't exist | `no-report` |

## Examples

```groovy
trivySummary('trivy-fs.json')        // → 'HIGH=2, MEDIUM=5'
trivySummary('trivy-image.json')     // → 'clean'
trivySummary('missing.json')         // → 'no-report'
```

Adding it to a PR comment:

```groovy
postScanSummary(cfg, [Trivy: trivySummary('trivy-image.json')])
```

The same script on a laptop:

```bash
trivy image --format json --output trivy.json ghcr.io/acme/orders-api:1.4.0
python3 resources/com/platformpipelines/scripts/trivy_summary.py trivy.json
# → CRITICAL=1, HIGH=3
```

## How it fits

Called from [scanTrivy](scanTrivy.md) after each scan. Parsing runs in
`resources/com/platformpipelines/scripts/trivy_summary.py` via
[useScript](../utilities/useScript.md).

## Source

[`vars/trivySummary.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/trivySummary.groovy)
