# updateManifest

The CI-to-CD handoff. Jenkins never talks to the cluster — it commits a new
image tag to the GitOps repo and Argo CD converges.

Rollback is a git revert, and cluster state is auditable from git history
without asking Jenkins anything.

## Syntax

```groovy
updateManifest(
    cfg  : cfg,
    env  : envCfg,
    image: '<repo>:<tag>'
)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |
| `env` | `Map` | yes | — | Target environment config (named `env` here, not `envCfg`). |
| `image` | `String` | yes | — | Full image reference with tag, e.g. `ghcr.io/acme/orders-api:1.4.0`. |

Reads `env.APP_VERSION`, `env.GIT_COMMIT`, `env.BUILD_URL`,
`env.DEPLOY_APPROVER` for the commit message.

### Config keys read

| Key | Default | Sample value |
|---|---|---|
| `gitopsRepo` | — | `acme/gitops-manifests` |
| `gitopsBranch` | `main` | `main` |
| `appName` | — | `orders-api` |
| `environments[].name` | — | `prod` |
| `environments[].manifestPath` | — | `apps/orders-api/prod/kustomization.yaml` |

## Returns

Nothing. Commits the change, or logs
`Manifest already at ghcr.io/acme/orders-api:1.4.0 — nothing to commit` and
returns. Fails the build if the manifest can't be read or the commit fails.

## Examples

```groovy
def prod = cfg.environments.find { it.name == 'prod' }
updateManifest(cfg: cfg, env: prod, image: "${cfg.imageRepo}:${env.IMAGE_TAG}")
```

Commit created in `acme/gitops-manifests@main`:

```
deploy(prod): orders-api 1.4.0

Image: ghcr.io/acme/orders-api:1.4.0
Commit: ab12cd3ef4567890ab12cd3ef4567890ab12cd3e
Build: https://jenkins.acme.internal/job/orders-api/job/main/42/
Approved-by: sam.lee
```

With the file change:

```diff
 images:
   - name: ghcr.io/acme/orders-api
-    newTag: 1.3.2
+    newTag: 1.4.0
```

`Approved-by` falls back to whoever triggered the build
([logActor](../other/logActor.md)) when there was no approval gate.

Rolling back:

```bash
git -C gitops-manifests revert <commit sha> && git -C gitops-manifests push
```

## How it fits

Reads the current manifest via [githubFetchFile](../other/githubFetchFile.md),
rewrites it with [manifestBumpImage](manifestBumpImage.md), and commits via
[githubCommitFile](../other/githubCommitFile.md). Called from
[deployGitops](../cloud/deployGitops.md).

## Source

[`vars/updateManifest.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/updateManifest.groovy)
