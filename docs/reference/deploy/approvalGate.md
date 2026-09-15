# approvalGate

Manual gate before a protected environment.

Two things matter here: the approver is recorded in the audit trail and
carried into the GitOps commit message, and by default nobody can approve
their own deploy.

## Syntax

```groovy
approvalGate(Map cfg, Map envCfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |
| `envCfg` | `Map` | yes | — | One entry from `cfg.environments`. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `environments[].name` | — | `prod` | Shown in the prompt and logs. |
| `environments[].approvers` | `[]` | `[platform-leads, jane.doe]` | Jenkins users or groups allowed to approve. |
| `environments[].approvalTimeoutMinutes` | `60` | `240` | How long to wait before failing. |
| `approval.allowSelfApproval` | `false` | `true` | `true` lets the person who triggered the build approve it. |
| `appName` | — | `orders-api` | Shown in the prompt. |

## Returns

Nothing. Sets `env.DEPLOY_APPROVER` to the approver's user id. Fails the
build when:

| Situation | Message |
|---|---|
| approver triggered the build (and self-approval isn't allowed) | `Self-approval blocked: jane.doe triggered this build and cannot approve their own deploy to prod` |
| someone clicks **Abort** | `Deploy to prod rejected by sam.lee` |
| nobody answers in time | `Approval for prod timed out after 240 minutes` |

## Examples

```yaml
# .ci/config.yaml
environments:
  - name: prod
    manifestPath: apps/orders-api/prod/kustomization.yaml
    requiresApproval: true
    approvers: [platform-leads, jane.doe]
    approvalTimeoutMinutes: 240
approval:
  allowSelfApproval: false
```

```groovy
def prod = cfg.environments.find { it.name == 'prod' }
approvalGate(cfg, prod)
echo "approved by ${env.DEPLOY_APPROVER}"     // → approved by sam.lee
```

What the approver sees in Jenkins:

```
Deploy orders-api 1.4.0 to prod?
REASON: [ Change reference or reason (recorded in the audit log) ]
[ Deploy to prod ]   [ Abort ]
```

Log and audit output:

```
====================================================================
  Approval required: prod
====================================================================
[INFO]  Approvers: platform-leads, jane.doe
[INFO]  Approved by sam.lee
[AUDIT] deploy.approved [environment:prod, approver:sam.lee, requester:jane.doe, reason:CHG-1042]
```

A small team where the author may approve:

```yaml
approval:
  allowSelfApproval: true
```

## How it fits

Prompts via [requestApproval](requestApproval.md), then compares the result
with [logActor](../utilities/logActor.md). Called by
[deployToEnvironment](deployToEnvironment.md) (gitops, before the
bump), [deployTerraform](../infrastructure/deployTerraform.md) and
[deployCloudFormation](../infrastructure/deployCloudFormation.md) (between plan and
apply) when `requiresApproval` is `true`.

## Source

[`vars/approvalGate.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/approvalGate.groovy)
