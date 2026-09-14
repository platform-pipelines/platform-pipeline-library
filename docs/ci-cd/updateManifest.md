# updateManifest

The CI-to-CD handoff. Jenkins never talks to the cluster — it commits a new
image tag to the GitOps repo and Argo CD converges.

Rollback is a git revert, and cluster state is auditable from git history
without asking Jenkins anything.

## Signature

```groovy
def call(Map args)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `args.cfg` | `Map` | Pipeline config; reads `cfg.gitopsRepo`/`gitopsBranch`/`appName`. |
| `args.env` | `Map` | Target environment config; reads `envCfg.name`/`manifestPath`. |
| `args.image` | `String` | Full image reference to bump the manifest to. |

## Returns

Nothing. Commits the manifest change, or logs and returns if the manifest is
already at the target image.

## Usage

```groovy
updateManifest(cfg: cfg, env: envCfg, image: "${cfg.imageRepo}:${env.IMAGE_TAG}")
```

Reads the current manifest via [githubFetchFile](../other/githubFetchFile.md),
rewrites it with [manifestBumpImage](manifestBumpImage.md), and commits via
[githubCommitFile](../other/githubCommitFile.md). The commit message records
who approved the deploy. Called from
[deployGitops](../cloud/deployGitops.md).

## Source

[`vars/updateManifest.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/updateManifest.groovy)
