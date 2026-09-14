# configLoad

Reads `.ci/config.yaml`, layers it over defaults, applies inline overrides,
validates, and returns a plain `Map`. This is the entry point almost every
other step's `cfg` argument ultimately comes from.

## Signature

```groovy
def call(Map overrides = [:])
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `overrides` | `Map` | Inline overrides merged in last; the `configFile` key picks the YAML path (default `.ci/config.yaml`). |

## Returns

The fully merged and validated config `Map`. Throws if
[`configValidate`](configValidate.md) finds any problems.

## Usage

```groovy
def cfg = configLoad()
def cfg = configLoad([configFile: '.ci/other.yaml', quality: [minCoverage: 90]])
```

## How it fits together

1. Reads the YAML file (if present) with `readYaml`; otherwise logs a warning and falls back to defaults + overrides only.
2. Merges [`configDefaults`](configDefaults.md) → the YAML → `overrides`, each step via [`configMerge`](configMerge.md).
3. Merges [`configEnvDefaults`](configEnvDefaults.md) into each `environments:` entry, defaulting `namespace` to the environment's `name`.
4. Derives `deployStrategy` and forces `containerize`/`sbom`/`signImage` off for infrastructure repos (`buildTool` in [`configInfraTools`](../cloud/configInfraTools.md)) — infra repos deploy by applying a plan, not by shipping a container image.
5. Runs [`configValidate`](configValidate.md) and raises a single error listing every problem found, rather than failing on the first.

## Source

[`vars/configLoad.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configLoad.groovy)
