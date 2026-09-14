# argoSync

Waits for Argo CD to converge. Argo would sync on its own, but blocking here
means a green build actually means "the deploy landed", which is what
people assume it means.

## Signature

```groovy
def call(Map cfg, Map envCfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; `cfg.appName` names the Argo app. |
| `envCfg` | `Map` | Target environment config; `envCfg.name` suffixes the Argo app name. |

## Returns

Nothing — blocks until Argo CD reports the app healthy and synced, or times
out after 10 minutes.

## Usage

```groovy
argoSync(cfg, envCfg)
```

Called by [deployGitops](deployGitops.md) after the manifest bump is
committed. Runs the `argocd` CLI inside a tool container via
[`inToolContainer`](../ci-cd/inToolContainer.md), authenticating with the
`argocd-token` credential.

## Source

[`vars/argoSync.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/argoSync.groovy)
