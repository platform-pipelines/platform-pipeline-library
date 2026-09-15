# Deployments

The Deploy stage runs once per environment the branch reaches, in the order
they are declared. [`configEnvironmentsFor`](../reference/config/configEnvironmentsFor.md)
matches each environment's `branchPattern` against the branch, and
[`deployToEnvironment`](../reference/deploy/deployToEnvironment.md) picks the
strategy.

## Strategies

`deployStrategy` selects one:

| Strategy | Step | What happens |
|---|---|---|
| `gitops` (default for apps) | [deployGitops](../reference/deploy/deployGitops.md) | Commit the new image tag to the GitOps repo ([manifestBumpImage](../reference/deploy/manifestBumpImage.md)); Argo CD syncs ([argoSync](../reference/deploy/argoSync.md)) |
| `ecs` | [deployEcs](../reference/deploy/deployEcs.md) | Register a task definition revision, update the service, wait, roll back on failure |
| `terraform` | [deployTerraform](../reference/infrastructure/deployTerraform.md) | Derived from `buildTool: terraform`; see [Infrastructure as code](infrastructure-as-code.md) |
| `cloudformation` | [deployCloudFormation](../reference/infrastructure/deployCloudFormation.md) | Derived from `buildTool: cloudformation`; see [Infrastructure as code](infrastructure-as-code.md) |

```yaml
imageRepo: ghcr.io/acme/orders-api
gitopsRepo: acme/gitops-manifests
environments:
  - name: dev
    manifestPath: apps/orders-api/dev/kustomization.yaml
    branchPattern: "*"
  - name: prod
    manifestPath: apps/orders-api/prod/kustomization.yaml   # Argo CD app: orders-api-prod
    requiresApproval: true
    approvers: [platform-leads]
```

**Jenkins never touches the cluster.** The last CI step commits an image tag to
the GitOps repo, and Argo converges. Rollback is `git revert`.

## Approvals

`requiresApproval: true` adds an [approvalGate](../reference/deploy/approvalGate.md).
Where it sits depends on the strategy:

- **gitops**: before the manifest commit, because once committed Argo acts on it.
- **infra**: between plan and apply, so the approver sees the actual diff
  rather than a general intention to deploy.

Approvers cannot approve their own builds unless `approval.allowSelfApproval: true`.

## Promotion and rollback

[`cdPipeline()`](../reference/pipelines/cdPipeline.md) deploys without rebuilding.
It either promotes the tag that the `promoteFrom` environment currently runs
([cdResolveImage](../reference/deploy/cdResolveImage.md)), or deploys a given
`IMAGE_TAG` to roll back. It checks that the tag exists in the registry first.
See the
[cd-promotion example](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/cd-promotion).

## Next

[Infrastructure as code](infrastructure-as-code.md)
