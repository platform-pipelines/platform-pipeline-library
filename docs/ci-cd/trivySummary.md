# trivySummary

One-line severity tally from a Trivy JSON report, for the audit log.

## Signature

```groovy
def call(String jsonReport)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `jsonReport` | `String` | Path to a Trivy JSON report file. |

## Returns

A one-line severity summary, or `'no-report'` if the file doesn't exist.

## Usage

```groovy
def summary = trivySummary('trivy-fs.json')
```

Called from [scanTrivy](scanTrivy.md) after each scan. The parsing itself
runs in `resources/com/platformpipelines/scripts/trivy_summary.py` via
[useScript](../other/useScript.md).

## Source

[`vars/trivySummary.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/trivySummary.groovy)
