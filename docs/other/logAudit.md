# logAudit

Appends one JSON line to `.ci-audit.jsonl`, archived at the end of the run.

!!! note "Why a file, not just console output"
    Console logs rotate; this file is the durable answer to "who deployed
    what, when, from which commit" when someone asks six months later.

## Signature

```groovy
def call(String action, Map details = [:])
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `action` | `String` | Short dotted event name, e.g. `'infra.changeset'`. |
| `details` | `Map` | Extra fields merged into the JSON record. |

## Returns

Nothing. Appends a record (`timestamp`, `action`, `job`, `build`, `commit`,
`branch`, `actor` via [`logActor`](logActor.md), plus `details`) to
`.ci-audit.jsonl` and echoes an `[AUDIT]` line.

## Usage

```groovy
logAudit('infra.changeset', [environment: envCfg.name, stack: stack])
```

## Source

[`vars/logAudit.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/logAudit.groovy)
