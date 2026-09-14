# imageExtraTags

Computes the moving tags published alongside the immutable version tag.

## Signature

```groovy
def call()
```

## Returns

`List<String>` of extra tags: the git short SHA, plus `latest` when building
on `main` or `master`.

## Usage

```groovy
def tags = imageExtraTags()
```

Used by [buildImageBuildah](buildImageBuildah.md) and
[kanikoArgs](kanikoArgs.md) to compute the full set of tags/destinations for
an image push.

## Source

[`vars/imageExtraTags.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/imageExtraTags.groovy)
