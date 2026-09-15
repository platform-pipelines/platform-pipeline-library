# deployGitops

Application deploy: bump the image tag in the GitOps manifest repo and let
Argo CD converge. Jenkins never touches the cluster.

## Syntax

```groovy
deployGitops(Map cfg, Map envCfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |
| `envCfg` | `Map` | yes | — | One entry from `cfg.environments`. |

Reads `env.IMAGE_TAG` (set by [initPipeline](../pipelines/initPipeline.md)).

### Config keys read

| Key | Default | Sample value | Used for |
|---|---|---|---|
| `imageRepo` | — | `ghcr.io/acme/orders-api` | Image to deploy: `<imageRepo>:<IMAGE_TAG>`. |
| `gitopsRepo` | — | `acme/gitops-manifests` | Repo holding the manifests. |
| `gitopsBranch` | `main` | `main` | Branch to commit to. |
| `appName` | — | `orders-api` | Commit message; Argo CD app name. |
| `environments[].name` | — | `prod` | Commit message; Argo CD app name `orders-api-prod`. |
| `environments[].manifestPath` | — | `apps/orders-api/prod/kustomization.yaml` | File to rewrite. |
| `environments[].namespace` | `name` | `orders-prod` | Recorded in the audit log. |

## Returns

Nothing. Commits the manifest change (if any) and waits for Argo CD to report
the app synced and healthy.

## Examples

```yaml
# .ci/config.yaml
appName: orders-api
buildTool: python
imageRepo: ghcr.io/acme/orders-api
gitopsRepo: acme/gitops-manifests
environments:
  - name: prod
    namespace: orders-prod
    manifestPath: apps/orders-api/prod/kustomization.yaml
    branchPattern: main
```

```groovy
deployGitops(cfg, cfg.environments[0])
```

With `IMAGE_TAG=1.4.0` this:

1. Rewrites `apps/orders-api/prod/kustomization.yaml` in `acme/gitops-manifests@main`:

    ```diff
     images:
       - name: ghcr.io/acme/orders-api
    -    newTag: 1.3.2
    +    newTag: 1.4.0
    ```

2. Records `[AUDIT] deploy [environment:prod, image:ghcr.io/acme/orders-api:1.4.0, namespace:orders-prod]`.
3. Waits for Argo CD app `orders-api-prod` to be synced and healthy.

## How it fits

Called by [deployToEnvironment](deployToEnvironment.md) when
`cfg.deployStrategy == 'gitops'` (after the approval gate, if any). Uses
[`updateManifest`](updateManifest.md) to commit the bump, then
[argoSync](argoSync.md) to wait for convergence.

## Source

[`vars/deployGitops.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/deployGitops.groovy)
