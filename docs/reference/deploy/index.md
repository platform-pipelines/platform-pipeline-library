# Deploy

Application deploys. [deployToEnvironment](deployToEnvironment.md) reads
`cfg.deployStrategy` for each environment and calls the matching deploy step.
Terraform and CloudFormation deploys are under
[Infrastructure](../infrastructure/index.md). For the narrative, see the
[Deployments guide](../../guides/deployments.md).

- **GitOps:** [deployGitops](deployGitops.md) bumps the image tag in the manifest repo
  ([updateManifest](updateManifest.md), [manifestBumpImage](manifestBumpImage.md)), then
  [argoSync](argoSync.md) waits for Argo CD to report it synced and healthy.
- **ECS:** [deployEcs](deployEcs.md), [ecsTaskDefinitionForImage](ecsTaskDefinitionForImage.md).
- **Approvals:** [approvalGate](approvalGate.md), [requestApproval](requestApproval.md).
- **Promotion:** [cdResolveImage](cdResolveImage.md), [manifestCurrentImage](manifestCurrentImage.md).
- **Releases:** [publishArtifact](publishArtifact.md) attaches build output to the `v<version>` GitHub release.

```yaml
imageRepo: ghcr.io/acme/orders-api
gitopsRepo: acme/gitops-manifests
environments:
  - name: prod
    manifestPath: apps/orders-api/prod/kustomization.yaml   # Argo CD app: orders-api-prod
```

## Quick reference

| Step | Syntax | Returns |
|---|---|---|
| [deployToEnvironment](deployToEnvironment.md) | `deployToEnvironment(cfg, envCfg)` | — |
| [deployGitops](deployGitops.md) | `deployGitops(cfg, envCfg)` | — |
| [argoSync](argoSync.md) | `argoSync(cfg, envCfg)` | — |
| [approvalGate](approvalGate.md) | `approvalGate(cfg, envCfg)` | — (sets `DEPLOY_APPROVER`) |
| [updateManifest](updateManifest.md) | `updateManifest(cfg: cfg, env: envCfg, image: 'ghcr.io/acme/api:1.4.0')` | — |
| [manifestBumpImage](manifestBumpImage.md) | `manifestBumpImage(yamlText, 'ghcr.io/acme/api:1.4.0')` | updated `String` |
