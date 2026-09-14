# generateSbom

Produces a CycloneDX SBOM for the built image and attaches it to the build.
Trivy generates it, so no extra tool is needed. An SBOM is what lets you
answer "are we affected by this CVE" in minutes across every deployed
service, instead of rebuilding each one to find out.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; `cfg.quality.sbom` and `cfg.imageRepo` are read. |

## Returns

Nothing — a no-op unless `cfg.quality.sbom` is true. Otherwise archives
`sbom.cdx.json` and logs a component summary.

## Usage

```groovy
generateSbom(cfg)
```

Runs Trivy via [inToolContainer](inToolContainer.md), and summarizes the
result with the bundled `sbom_summary.py` script via
[useScript](../other/useScript.md). Feeds [signImage](signImage.md)'s
optional SBOM attestation.

## Source

[`vars/generateSbom.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/generateSbom.groovy)
