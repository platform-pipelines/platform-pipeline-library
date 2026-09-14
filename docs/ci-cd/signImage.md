# signImage

Signs the built image with cosign, keyless, using the build's OIDC identity.
Keyless means no private key to store or rotate: the signature is bound to
the workload identity that produced it and recorded in a transparency log.

!!! warning "Signing only matters if something verifies"
    Pair this with an admission policy (Kyverno, Sigstore
    policy-controller) that rejects unsigned images, or signing is just
    paperwork.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.quality.signImage`/`cfg.quality.sbom` and `cfg.imageRepo`. |

## Returns

Nothing — a no-op unless `cfg.quality.signImage` is true and
`env.IMAGE_DIGEST` is set. Signs the digest, never the tag, since a tag can
be repointed at different content after signing but a digest can't. Also
attaches the SBOM as a signed attestation when `cfg.quality.sbom` is on.

## Usage

```groovy
signImage(cfg)
```

Must run after [buildImage](buildImage.md), which sets `env.IMAGE_DIGEST`.
Called by [standardPipeline](standardPipeline.md).

## Source

[`vars/signImage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/signImage.groovy)
