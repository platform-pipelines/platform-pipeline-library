# configDefaults

Baseline every consuming repo inherits. Anything not set in `.ci/config.yaml`
comes from here, so adding a new capability with a safe default does not
require touching every repo.

## Signature

```groovy
def call()
```

## Returns

`Map` of every recognized top-level config key with its default value —
`appName`, `buildTool`, `containerize`, `infra`, `environments`, `lint`,
`quality`, `publish`, `approval`, `notify`, `extra`, and more.

This map is also the schema: [`configUnknownKeys`](configUnknownKeys.md) warns
about any key a repo writes that does not appear here. A new key therefore has
to be added here, even when its default is `null`. `extra` is the one
free-form section — the pipeline never reads or checks it.

## Usage

```groovy
def defaults = configDefaults()
```

Called by [`configLoad`](configLoad.md), which merges it under whatever a
repo's `.ci/config.yaml` declares via [`configMerge`](configMerge.md). See
also [`configEnvDefaults`](configEnvDefaults.md) for the per-environment
equivalent.

## Source

[`vars/configDefaults.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configDefaults.groovy)
