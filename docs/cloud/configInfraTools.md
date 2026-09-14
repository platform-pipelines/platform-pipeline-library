# configInfraTools

`buildTool` values that describe infrastructure rather than an application.
These skip container build, SBOM, signing, and artifact publishing, and
deploy by applying a plan instead of bumping a manifest.

## Syntax

```groovy
configInfraTools()
```

## Parameters

None.

## Returns

`List<String>` — `['terraform', 'cloudformation']`.

What changes for a repo whose `buildTool` is in this list:

| Setting | Application repo | Infra repo |
|---|---|---|
| `containerize` | `true` (default) | forced `false` |
| `deployStrategy` | `gitops` | same as `buildTool` |
| `quality.sbom` / `quality.signImage` | as configured | forced `false` |
| Trivy scan | [scanTrivy](../ci-cd/scanTrivy.md) (`fs`) | [scanIac](../ci-cd/scanIac.md) (`trivy config`) |
| Artifact publish | [publishArtifact](../ci-cd/publishArtifact.md) | skipped |

## Examples

```groovy
configInfraTools()                        // → ['terraform', 'cloudformation']
'terraform' in configInfraTools()         // → true
cfg.buildTool in configInfraTools()       // same check as isInfraRepo(cfg)
```

```yaml
# .ci/config.yaml — nothing else needs restating
appName: platform-network
buildTool: terraform
```

## How it fits

Used by [isInfraRepo](isInfraRepo.md), by
[`configLoad`](../other/configLoad.md) to derive the values above, and by
[`configValidate`](../other/configValidate.md) to reject
`containerize: true` on an infra repo.

## Source

[`vars/configInfraTools.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configInfraTools.groovy)
