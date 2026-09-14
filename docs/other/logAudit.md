# logAudit

Appends one JSON line to `.ci-audit.jsonl`, archived at the end of the run.

!!! note "Why a file, not just console output"
    Console logs rotate; this file is the durable answer to "who deployed
    what, when, from which commit" when someone asks six months later.

## Syntax

```groovy
logAudit(String action)
logAudit(String action, Map details)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `action` | `String` | yes | — | Short dotted event name, e.g. `'deploy.approved'`. |
| `details` | `Map` | no | `[:]` | Extra fields added to the JSON record. A key here overrides a standard field of the same name. |

## Returns

Nothing. Appends a record to `.ci-audit.jsonl` in the workspace and echoes an
`[AUDIT]` line. Every record has these standard fields:

| Field | Source | Sample |
|---|---|---|
| `timestamp` | now, UTC | `2026-09-14T09:31:07Z` |
| `action` | argument | `deploy.approved` |
| `job` | `env.JOB_NAME` | `acme/orders-api/main` |
| `build` | `env.BUILD_NUMBER` | `42` |
| `commit` | `env.GIT_COMMIT` | `ab12cd3ef456…` |
| `branch` | `env.BRANCH_NAME` | `main` |
| `actor` | [`logActor`](logActor.md) | `jane.doe` |

## Examples

```groovy
logAudit('pipeline.start')

logAudit('deploy.approved', [environment: 'prod', approver: 'sam.lee', reason: 'CHG-1042'])
```

Log line:

```
[AUDIT] deploy.approved [environment:prod, approver:sam.lee, reason:CHG-1042]
```

Line appended to `.ci-audit.jsonl`:

```json
{"timestamp":"2026-09-14T09:31:07Z","action":"deploy.approved","job":"acme/orders-api/main","build":"42","commit":"ab12cd3ef4567890ab12cd3ef4567890ab12cd3e","branch":"main","actor":"jane.doe","environment":"prod","approver":"sam.lee","reason":"CHG-1042"}
```

Querying an archived file later:

```bash
jq -c 'select(.action | startswith("deploy"))' .ci-audit.jsonl
```

## Actions the library records

`pipeline.start`, `image.push`, `image.signed`, `sbom.generated`,
`artifact.publish`, `quality.coverage`, `quality.gate`, `security.scan`,
`security.iac`, `security.secrets`, `deploy.approved`, `deploy.rejected`,
`deploy.timeout`, `deploy.self_approval_blocked`, `deploy`, `deploy.synced`,
`infra.plan`, `infra.changeset`, `infra.apply`.

## Source

[`vars/logAudit.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/logAudit.groovy)
