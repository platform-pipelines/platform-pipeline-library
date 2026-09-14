# configInfraTools

`buildTool` values that describe infrastructure rather than an application.
These skip container build, SBOM, signing, and artifact publishing, and
deploy by applying a plan instead of bumping a manifest.

## Signature

```groovy
def call()
```

## Returns

A `List` of `buildTool` values that are infrastructure-as-code, not an
application: `['terraform', 'cloudformation']`.

## Usage

```groovy
def infraTools = configInfraTools()
```

Used by [isInfraRepo](isInfraRepo.md) and by
[`configLoad`](../other/configLoad.md) to derive `containerize`,
`deployStrategy`, and the SBOM/sign quality flags.

## Source

[`vars/configInfraTools.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configInfraTools.groovy)
