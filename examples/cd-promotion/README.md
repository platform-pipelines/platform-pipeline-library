# cd-promotion example

Continuous **delivery** with no rebuild: `cdPipeline()` takes an image that
`standardPipeline()` already built, scanned and pushed, and deploys it to one
environment. Promotion reads what the previous environment is *actually
running*, so prod receives exactly the bytes staging tested.

```
 CI (multibranch, Jenkinsfile)                 CD (pipeline job, Jenkinsfile.cd)
 ─────────────────────────────                 ─────────────────────────────────
 lint → build → test → scan → push image       ENVIRONMENT=staging  → promotes what dev runs
                         └→ Deploy: dev        ENVIRONMENT=prod     → promotes what staging runs
                                                                      (approval gate)
                                               ENVIRONMENT=prod IMAGE_TAG=1.3.2 → rollback
```

## Two jobs, one repo

| Job | Type | Script path | Entry point |
|---|---|---|---|
| `inventory-api` | Multibranch Pipeline | `Jenkinsfile` | `standardPipeline()` |
| `inventory-api-cd` | Pipeline (from SCM, branch `main`) | `Jenkinsfile.cd` | `cdPipeline()` |

In a real repo `Jenkinsfile` is the CI file and this directory's `Jenkinsfile`
becomes `Jenkinsfile.cd` (the [go-service](../go-service) and
[aws-ecs-service](../aws-ecs-service) examples show both side by side). Both
read `.ci/config.yaml`, so environments and approvers are declared once.

Run the CD job once with no parameters: the first run registers the
`ENVIRONMENT`, `IMAGE_TAG` and `DRY_RUN` parameters and fails asking for an
environment. Every run after that is *Build with Parameters*.

## Parameters

| Parameter | Empty | Set |
|---|---|---|
| `ENVIRONMENT` | fails, listing the declared environments | the environment to deploy |
| `IMAGE_TAG` | promote the tag running in `promoteFrom` | deploy exactly this tag — also how you roll back |
| `DRY_RUN` | deploy | resolve the tag and check the registry, change nothing |

## Stages

| Stage | What runs |
|---|---|
| Resolve | checkout, [configLoad](../../docs/other/configLoad.md), [cdResolveImage](../../docs/ci-cd/cdResolveImage.md) (reads the source environment's manifest or ECS service), [imageDigest](../../docs/ci-cd/imageDigest.md) (fails if the tag is not in the registry) |
| Deploy | [deployToEnvironment](../../docs/cloud/deployToEnvironment.md): [approvalGate](../../docs/ci-cd/approvalGate.md) when required, then the GitOps manifest bump and Argo CD sync (or the ECS rollout) |

The build name reads `#12 prod ← 1.4.0`, and the audit log records the digest
that was deployed, who approved it, and whether it was a promotion or an
explicit tag.

## What each section demonstrates

- **`branchPattern: none`** on staging and prod — no branch is named `none`,
  so CI never deploys there; only the CD job does. Leave `branchPattern: main`
  on an environment if you want CI to deploy it too.
- **`promoteFrom`** — the chain dev → staging → prod. `configValidate` rejects
  a `promoteFrom` naming an environment that does not exist.
- **approval** — prod waits for `platform-leads` or `inventory-oncall`, and the
  person who started the CD run cannot approve it.
- **notify** — `on: always`: every deploy is announced.

## Requirements

Same credentials as the CI pipeline's deploy stage: `github-token` (read and
commit the GitOps repo), `argocd-token` + `ARGOCD_SERVER`, and registry
credentials (`ghcr-credentials`, or ECR through `aws-credentials`) so
`imageDigest` can look the tag up.
