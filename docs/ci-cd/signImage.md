# signImage

Signs the built image with cosign, keyless, using the build's OIDC identity.
Keyless means no private key to store or rotate: the signature is bound to
the workload identity that produced it and recorded in a transparency log.

!!! warning "Signing only matters if something verifies"
    Pair this with an admission policy (Kyverno, Sigstore
    policy-controller) that rejects unsigned images, or signing is just
    paperwork.

## Syntax

```groovy
signImage(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

Reads `env.IMAGE_DIGEST`, set by [buildImage](buildImage.md).

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `quality.signImage` | `false` | `true` | Turns signing on. Requires `containerize: true`. |
| `quality.sbom` | `true` | `true` | Also attaches `sbom.cdx.json` as a signed attestation (when the file exists). |
| `imageRepo` | — | `ghcr.io/acme/orders-api` | Image to sign. |

Credential: `cosign-oidc-token` (string) — an OIDC identity token cosign
exchanges for a short-lived signing certificate.

## Returns

Nothing. Does nothing when `quality.signImage` is `false`. Fails the build
with `Cannot sign: no image digest. buildImage must run first.` when
`env.IMAGE_DIGEST` is empty. Signs the digest, never the tag, since a tag can
be repointed at different content after signing but a digest can't.

## Examples

```yaml
# .ci/config.yaml
imageRepo: ghcr.io/acme/orders-api
quality:
  sbom: true
  signImage: true
```

```groovy
buildImage(cfg)          // sets IMAGE_DIGEST=sha256:7d9c…e41a
generateSbom(cfg)        // writes sbom.cdx.json
signImage(cfg)
```

Runs:

```bash
cosign sign --yes --identity-token $COSIGN_TOKEN ghcr.io/acme/orders-api@sha256:7d9c…e41a
cosign attest --yes --identity-token $COSIGN_TOKEN \
  --predicate sbom.cdx.json --type cyclonedx \
  ghcr.io/acme/orders-api@sha256:7d9c…e41a
```

Verifying from a laptop:

```bash
cosign verify ghcr.io/acme/orders-api:1.4.0 \
  --certificate-identity-regexp '.*' \
  --certificate-oidc-issuer https://token.actions.githubusercontent.com
```

(Use the issuer and identity your `cosign-oidc-token` actually comes from.)

## How it fits

Called by [standardPipeline](standardPipeline.md)'s `Package` stage after
[buildImage](buildImage.md) and [generateSbom](generateSbom.md).

## Source

[`vars/signImage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/signImage.groovy)
