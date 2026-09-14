# requestApproval

The Jenkins `input()` call behind every deploy approval, with rejection and
timeout distinguished so the audit log records which one happened.

## Syntax

```groovy
def answer = requestApproval(Map cfg, Map envCfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; `appName` appears in the prompt. |
| `envCfg` | `Map` | yes | — | Target environment. |

Reads `env.APP_VERSION` for the prompt.

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `environments[].name` | — | `prod` | Prompt text, button label, input id `deploy-prod`. |
| `environments[].approvers` | `[]` | `[platform-leads, jane.doe]` | Allowed submitters (comma-joined). |
| `environments[].approvalTimeoutMinutes` | `60` | `240` | Timeout. |

## Returns

A `Map` from the input step:

| Key | Sample value |
|---|---|
| `APPROVER` | `sam.lee` (the user who clicked the button) |
| `REASON` | `CHG-1042` (free text; may be empty) |

Fails the build on rejection or timeout:

| Outcome | Audit action | Error |
|---|---|---|
| **Abort** clicked | `deploy.rejected` | `Deploy to prod rejected by sam.lee` |
| timeout reached | `deploy.timeout` | `Approval for prod timed out after 240 minutes` |

## Examples

```groovy
def prod = cfg.environments.find { it.name == 'prod' }
def answer = requestApproval(cfg, prod)

echo "${answer.APPROVER} approved: ${answer.REASON}"
// → sam.lee approved: CHG-1042
```

The prompt shown in Jenkins:

```
Deploy orders-api 1.4.0 to prod?
REASON  [ Change reference or reason (recorded in the audit log) ]
                                    [ Deploy to prod ]  [ Abort ]
```

## How it fits

Called from [approvalGate](approvalGate.md), which also blocks self-approval
and records `deploy.approved`. Prefer calling `approvalGate`.

## Source

[`vars/requestApproval.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/requestApproval.groovy)
