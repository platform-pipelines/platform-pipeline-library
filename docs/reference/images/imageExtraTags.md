# imageExtraTags

Computes the moving tags published alongside the immutable version tag.

## Syntax

```groovy
imageExtraTags()
```

## Parameters

None. Reads `env.GIT_SHORT_SHA` and `env.BRANCH_NAME`.

## Returns

`List<String>`:

| Branch | Result (with `GIT_SHORT_SHA=ab12cd3`) |
|---|---|
| `main` or `master` | `['ab12cd3', 'latest']` |
| anything else | `['ab12cd3']` |

The version tag itself (`env.IMAGE_TAG`) is not included — callers add it.

## Examples

```groovy
imageExtraTags()                                   // on main → ['ab12cd3', 'latest']

def tags = [env.IMAGE_TAG] + imageExtraTags()      // → ['1.4.0', 'ab12cd3', 'latest']
tags.each { sh "crane tag ghcr.io/acme/orders-api:${env.IMAGE_TAG} ${it}" }
```

## How it fits

Used by [buildImageBuildah](buildImageBuildah.md) and
[kanikoArgs](kanikoArgs.md) to compute every tag an image is pushed with.

## Source

[`vars/imageExtraTags.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/imageExtraTags.groovy)
