# deployGitops

Application deploy: bump the image tag in the GitOps manifest repo and let
Argo CD converge. Jenkins never touches the cluster.

## Signature

```groovy
def call(Map cfg, Map envCfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.imageRepo`. |
| `envCfg` | `Map` | Target environment config; reads `envCfg.name`/`namespace`. |

## Returns

Nothing — commits the manifest bump and waits for Argo CD to sync.

## Usage

```groovy
deployGitops(cfg, envCfg)
```

Called by [deployToEnvironment](deployToEnvironment.md) when
`cfg.deployStrategy == 'gitops'`. Uses
[`updateManifest`](../ci-cd/updateManifest.md) to commit the bump, then
[argoSync](argoSync.md) to wait for convergence.

## Source

[`vars/deployGitops.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/deployGitops.groovy)
