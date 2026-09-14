# argoSync

Waits for Argo CD to converge. Argo would sync on its own, but blocking here
means a green build actually means "the deploy landed", which is what
people assume it means.

## Syntax

```groovy
argoSync(Map cfg, Map envCfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; `cfg.appName` is the first half of the Argo CD app name. |
| `envCfg` | `Map` | yes | — | Target environment; `envCfg.name` is the second half. |

The Argo CD application must be named **`<appName>-<environment name>`**.

### Config keys read

| Key | Default | Sample value |
|---|---|---|
| `appName` | — | `orders-api` |
| `environments[].name` | — | `prod` |

### Controller requirements

| Item | Kind | Sample value |
|---|---|---|
| `ARGOCD_SERVER` | environment variable | `argocd.acme.internal` |
| `argocd-token` | string credential | an Argo CD API token with `applications, get/sync` |

## Returns

Nothing. Blocks until `argocd app wait --health --sync` succeeds, then logs
`argocd app get -o wide`. Fails the build if the app isn't healthy and synced
within 10 minutes.

## Examples

```groovy
argoSync(cfg, [name: 'prod'])
```

With the sample config this runs:

```bash
argocd --server argocd.acme.internal --auth-token $ARGOCD_TOKEN --grpc-web \
  app wait orders-api-prod --health --sync --timeout 600
```

Log output:

```
====================================================================
  Argo CD: orders-api-prod
====================================================================
[INFO]  Name: orders-api-prod ... Sync Status: Synced ... Health Status: Healthy
[AUDIT] deploy.synced [app:orders-api-prod, environment:prod]
```

## How it fits

Called by [deployGitops](deployGitops.md) after the manifest bump is
committed. Runs the `argocd` CLI directly on toolbox agents, or in
`quay.io/argoproj/argocd:latest` otherwise, via
[`inToolContainer`](../ci-cd/inToolContainer.md).

## Source

[`vars/argoSync.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/argoSync.groovy)
