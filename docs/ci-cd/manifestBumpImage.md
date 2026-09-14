# manifestBumpImage

Rewrites the image reference inside a manifest, leaving everything else —
comments, ordering, unrelated containers — untouched.

This is the riskiest string handling in the library: a regex that silently
fails to match means the pipeline reports success while the old image stays
deployed.

!!! warning "`@NonCPS`"
    Pure regex/string manipulation with no pipeline steps other than
    `error()` — running it un-transformed avoids CPS overhead on every
    `replaceAll` closure invocation.

## Signature

```groovy
def call(String yamlText, String image)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `yamlText` | `String` | Manifest content (kustomization, plain Kubernetes manifest, or Helm values). |
| `image` | `String` | Full image reference including tag. |

## Returns

`yamlText` with the matching image/tag lines rewritten to the new tag.

## Usage

```groovy
def updated = manifestBumpImage(yamlText, 'ghcr.io/acme/api:1.4.0')
```

Handles three manifest shapes: `kustomization.yaml`'s `newTag:`, a plain
`image: repo:tag` line, and Helm values' `tag: "1.2.3"`. Called from
[updateManifest](updateManifest.md).

## Source

[`vars/manifestBumpImage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/manifestBumpImage.groovy)
