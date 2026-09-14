# sonarWaitForGate

Polls SonarQube for the quality gate result.

Uses the API rather than the plugin's `waitForQualityGate()`, which needs a
webhook back into Jenkins — awkward when Jenkins isn't publicly reachable.

## Syntax

```groovy
sonarWaitForGate(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; `quality.sonarProjectKey` is used for the audit record and dashboard link. |

Reads `.scannerwork/report-task.txt` (written by `sonar-scanner`),
`env.SONAR_HOST_URL`, and the `sonar-token` credential.

## Returns

Nothing. Polls every few seconds for up to 10 minutes, then:

| Gate result | Outcome |
|---|---|
| `OK` | commit status `ci/sonar` = success (`Quality gate passed`) |
| `ERROR` / `WARN` | commit status `ci/sonar` = failure; build fails: `SonarQube quality gate failed (ERROR) — https://sonar.acme.internal/dashboard?id=acme_orders-api` |
| no `report-task.txt` | `[WARN]  No report-task.txt — cannot check the quality gate`, returns |
| still pending after 10 min | build fails with a timeout |

## Examples

```groovy
sh "sonar-scanner ${sonarProperties(cfg)}"      // writes .scannerwork/report-task.txt
sonarWaitForGate(cfg)
```

Log output:

```
[INFO]  Quality gate: OK
[AUDIT] quality.gate [result:OK, project:acme_orders-api]
```

Checking a gate by hand, with the same script:

```bash
python3 resources/com/platformpipelines/scripts/sonar_gate.py \
  "https://sonar.acme.internal/api/ce/task?id=AY1…" https://sonar.acme.internal "$SONAR_TOKEN"
# → OK
```

## How it fits

Called from [scanSonar](scanSonar.md) when `cfg.quality.failOnQualityGate` is
`true`. Polling runs in
`resources/com/platformpipelines/scripts/sonar_gate.py` via
[useScript](../other/useScript.md).

## Source

[`vars/sonarWaitForGate.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/sonarWaitForGate.groovy)
