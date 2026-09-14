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

## Syntax

```groovy
manifestBumpImage(String yamlText, String image)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `yamlText` | `String` | yes | — | Manifest content: kustomization, plain Kubernetes manifest, or Helm values. |
| `image` | `String` | yes | — | Full image reference **with tag**, e.g. `ghcr.io/acme/api:1.4.0`. The tag is whatever follows the last `:`. |

## Returns

`yamlText` with matching lines rewritten. Returns the text unchanged when
nothing matches. Fails the build if `image` has no `:` at all:
`Image reference 'ghcr.io/acme/api' has no tag`.

## What gets rewritten

| Shape | Matches | Rewritten to |
|---|---|---|
| kustomization | any `newTag:` line | `newTag: <tag>` |
| plain manifest | `image: <same repo>:<anything>` | `image: <repo>:<tag>` |
| Helm values | any `tag:` line | `tag: "<tag>"` |

A leading `- ` (inline list item) is allowed on all three.

!!! warning "`newTag:` and `tag:` match every occurrence"
    Unlike `image:`, these two patterns aren't tied to the repository name.
    A file with several `newTag:`/`tag:` lines for different images will have
    all of them rewritten — keep one app's tag per manifest file.

## Examples

**kustomization.yaml:**

```groovy
def before = '''
images:
  - name: ghcr.io/acme/orders-api
    newTag: 1.3.2   # bumped by CI
'''
manifestBumpImage(before, 'ghcr.io/acme/orders-api:1.4.0')
```

```yaml
images:
  - name: ghcr.io/acme/orders-api
    newTag: 1.4.0
```

**Plain Deployment** — only the matching container changes:

```yaml
# before
containers:
  - image: ghcr.io/acme/orders-api:1.3.2
  - image: ghcr.io/acme/log-shipper:0.9.0
# after manifestBumpImage(text, 'ghcr.io/acme/orders-api:1.4.0')
containers:
  - image: ghcr.io/acme/orders-api:1.4.0
  - image: ghcr.io/acme/log-shipper:0.9.0
```

**Helm values:**

```yaml
# before
image:
  repository: ghcr.io/acme/orders-api
  tag: 1.3.2
# after
image:
  repository: ghcr.io/acme/orders-api
  tag: "1.4.0"
```

**Registry with a port:**

```groovy
manifestBumpImage('image: registry.local:5000/acme/api:1.3.2', 'registry.local:5000/acme/api:1.4.0')
// → 'image: registry.local:5000/acme/api:1.4.0'
```

## How it fits

Called from [updateManifest](updateManifest.md). Covered by
`ManifestBumpImageTest`.

## Source

[`vars/manifestBumpImage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/manifestBumpImage.groovy)
