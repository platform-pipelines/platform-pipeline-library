# manifestCurrentImage

The image tag a GitOps manifest currently deploys — the read side of
[manifestBumpImage](manifestBumpImage.md), recognising the same shapes.

## Syntax

```groovy
String manifestCurrentImage(String yamlText, String imageRepo)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `yamlText` | `String` | yes | — | Manifest content. |
| `imageRepo` | `String` | yes | — | Repository without a tag. |

## Returns

The tag, or `null` when the manifest has none for that repository. Shapes are
tried in this order:

| Shape | Sample line | Returns |
|---|---|---|
| plain manifest | `image: ghcr.io/acme/api:1.3.9` | `1.3.9` |
| kustomization | `newTag: 1.3.9` | `1.3.9` |
| Helm values | `tag: "1.3.9"` | `1.3.9` |

## Examples

```groovy
manifestCurrentImage('- image: registry.local:5000/acme/api:2.0.0', 'registry.local:5000/acme/api')  // '2.0.0'
```

## Source

[`vars/manifestCurrentImage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/manifestCurrentImage.groovy)
