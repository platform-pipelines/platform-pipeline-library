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
`quality`, `notify`, `extra`, and more.

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
