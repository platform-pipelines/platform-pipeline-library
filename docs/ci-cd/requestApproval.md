# requestApproval

The `input()` call, with rejection and timeout distinguished so the audit
log records which one happened.

## Signature

```groovy
def call(Map cfg, Map envCfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; used for `appName` and `env.APP_VERSION` context in the prompt. |
| `envCfg` | `Map` | Target environment config; reads `name`, `approvers`, `approvalTimeoutMinutes`. |

## Returns

The approver's username (the `APPROVER` submitter parameter). Throws if the
request is rejected or times out.

## Usage

```groovy
requestApproval(cfg, envCfg)
```

Called from [approvalGate](approvalGate.md), which does the self-approval
check on the result.

## Source

[`vars/requestApproval.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/requestApproval.groovy)
