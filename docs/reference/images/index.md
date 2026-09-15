# Images

Container image builds, registry auth, where steps run, and supply-chain
signing. For how to pick a builder, see
[Images & supply chain](../../guides/images-and-supply-chain.md).

- **Build:** [buildImage](buildImage.md) dispatches on `cfg.imageBuilder` to
  [buildImageKanikoDocker](buildImageKanikoDocker.md),
  [buildImageKanikoK8s](buildImageKanikoK8s.md) or
  [buildImageBuildah](buildImageBuildah.md).
- **Kaniko support:** [kanikoArgs](kanikoArgs.md), [kanikoDockerConfig](kanikoDockerConfig.md).
- **Tags, labels, digest:** [imageExtraTags](imageExtraTags.md), [imageLabels](imageLabels.md), [imageDigest](imageDigest.md).
- **Registry:** [withRegistryAuth](withRegistryAuth.md).
- **Where a step runs:** [inBuildContainer](inBuildContainer.md), [inContainer](inContainer.md), [inToolContainer](inToolContainer.md), [usingToolbox](usingToolbox.md).
- **Supply chain:** [generateSbom](generateSbom.md), [signImage](signImage.md).

```yaml
imageRepo: ghcr.io/acme/orders-api
dockerfile: Dockerfile
imageBuilder: kaniko-docker     # kaniko-docker | kaniko-k8s | buildah
quality:
  sbom: true
  signImage: true
```

## Quick reference

| Step | Syntax | Returns |
|---|---|---|
| [buildImage](buildImage.md) | `buildImage(cfg)` | — (sets `IMAGE_REF`, `IMAGE_DIGEST`) |
| [inBuildContainer](inBuildContainer.md) | `inBuildContainer(cfg) { sh 'make' }` | — |
| [inToolContainer](inToolContainer.md) | `inToolContainer('aquasec/trivy:latest') { … }` | value of the body |
