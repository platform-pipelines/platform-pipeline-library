# generateSbom

Produces a CycloneDX SBOM for the built image and attaches it to the build.
Trivy generates it, so no extra tool is needed. An SBOM is what lets you
answer "are we affected by this CVE" in minutes across every deployed
service, instead of rebuilding each one to find out.

## Syntax

```groovy
generateSbom(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

Reads `env.IMAGE_TAG`; the image must already be pushed by
[buildImage](buildImage.md).

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `quality.sbom` | `true` (forced `false` for infra repos) | `false` | `false` skips the step. |
| `imageRepo` | — | `ghcr.io/acme/orders-api` | Image to describe. |

## Returns

Nothing. Writes, archives and fingerprints `sbom.cdx.json`, and logs a
component summary.

## Examples

```yaml
imageRepo: ghcr.io/acme/orders-api
quality:
  sbom: true
```

```groovy
buildImage(cfg)
generateSbom(cfg)
```

Runs:

```bash
trivy image --format cyclonedx --output sbom.cdx.json --no-progress ghcr.io/acme/orders-api:1.4.0
```

Output:

```
====================================================================
  SBOM
====================================================================
[INFO]  SBOM: 214 components (pypi=180, deb=34)
[AUDIT] sbom.generated [image:ghcr.io/acme/orders-api:1.4.0, components:214 components (pypi=180, deb=34)]
```

Finding a vulnerable package later, from the archived file:

```bash
jq -r '.components[] | select(.name=="urllib3") | .version' sbom.cdx.json
```

## How it fits

Runs Trivy via [inToolContainer](inToolContainer.md) and summarises with the
bundled `sbom_summary.py` (see [useScript](../utilities/useScript.md)). Called
from [standardPipeline](../pipelines/standardPipeline.md)'s `Package` stage between
[buildImage](buildImage.md) and [signImage](signImage.md), which attaches the
SBOM as an attestation.

## Source

[`vars/generateSbom.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/generateSbom.groovy)
