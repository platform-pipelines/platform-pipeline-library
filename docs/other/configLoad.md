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
2. Restores `notify.on`: YAML 1.1 reads the bare key `on:` as boolean `true`, which used to make the setting silently ignored.
3. Logs a warning for every unknown or misspelled key via [`configUnknownKeys`](configUnknownKeys.md) (with a "did you mean" hint). Warnings never fail the build.
4. Merges [`configDefaults`](configDefaults.md) → the YAML → `overrides`, each step via [`configMerge`](configMerge.md).
5. Applies [`configDeprecatedKeys`](configDeprecatedKeys.md): an old `extra.*` key is copied to its new home with a deprecation warning, unless the new key is also set (the new key wins).
6. Merges [`configEnvDefaults`](configEnvDefaults.md) into each `environments:` entry, defaulting `namespace` to the environment's `name`.
7. Derives `deployStrategy` and forces `containerize`/`sbom`/`signImage` off for infrastructure repos (`buildTool` in [`configInfraTools`](../cloud/configInfraTools.md)) — infra repos deploy by applying a plan, not by shipping a container image.
8. Runs [`configValidate`](configValidate.md) and raises a single error listing every problem found, rather than failing on the first.

## Source

[`vars/configLoad.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configLoad.groovy)
