# cdResolveImage

Decides which image tag a [cdPipeline](cdPipeline.md) run deploys: an explicit
tag wins; otherwise the tag currently running in the environment's
`promoteFrom` environment, read from the GitOps manifest or the ECS service.

## Syntax

```groovy
String cdResolveImage(Map cfg, Map envCfg, String requestedTag)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |
| `envCfg` | `Map` | yes | — | Target environment. |
| `requestedTag` | `String` | no | — | Tag from `IMAGE_TAG`; empty or `null` means promote. |

## Returns

The tag to deploy. Errors when:

| Condition | Message |
|---|---|
| tag has characters outside `[A-Za-z0-9._-]` | `IMAGE_TAG '1.0; rm -rf /' is not a valid image tag` |
| no tag and no `promoteFrom` | `IMAGE_TAG is required: staging has no promoteFrom environment…` |
| source runs no tag for `imageRepo` | `Could not find a ghcr.io/acme/api tag running in staging to promote` |

## Examples

```groovy
env.IMAGE_TAG = cdResolveImage(cfg, prodEnv, params.IMAGE_TAG)
```

| Strategy | How the source tag is read |
|---|---|
| `gitops` | [githubFetchFile](../other/githubFetchFile.md) of the source `manifestPath`, then [manifestCurrentImage](manifestCurrentImage.md) |
| `ecs` | `aws ecs describe-services` → `describe-task-definition` → image of `ecsContainer` (default `appName`), under the source environment's credentials |

## Source

[`vars/cdResolveImage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/cdResolveImage.groovy)
