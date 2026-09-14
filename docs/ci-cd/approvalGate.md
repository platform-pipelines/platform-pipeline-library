# approvalGate

Manual gate before a protected environment.

Two things matter here: the approver is recorded in the audit trail and
carried into the GitOps commit message, and by default nobody can approve
their own deploy.

## Signature

```groovy
def call(Map cfg, Map envCfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; `cfg.extra.allowSelfApproval` opts out of the self-approval block. |
| `envCfg` | `Map` | Target environment config; `envCfg.name` and `envCfg.approvers` are used. |

## Returns

Nothing. Sets `env.DEPLOY_APPROVER` and throws if the approver is the
requester (unless self-approval is explicitly allowed).

## Usage

```groovy
approvalGate(cfg, envCfg)
```

Prompts via [requestApproval](requestApproval.md), then checks the result
against [logActor](../other/logActor.md) (who triggered the build). Called
by [deployGitops](../cloud/deployGitops.md) and
[deployCloudFormation](../cloud/deployCloudFormation.md) when
`envCfg.requiresApproval` is `true`.

## Source

[`vars/approvalGate.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/approvalGate.groovy)
