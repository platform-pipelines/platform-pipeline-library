# configLoad

Reads `.ci/config.yaml`, layers it over defaults, applies inline overrides,
validates, and returns a plain `Map`. This is the entry point almost every
other step's `cfg` argument ultimately comes from.

## Syntax

```groovy
configLoad()                                   // read .ci/config.yaml
configLoad(Map overrides)                      // read .ci/config.yaml, then apply overrides
configLoad(configFile: '<path>', ...)          // read a different YAML file
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `overrides` | `Map` | no | `[:]` | Config merged in last, over the YAML. Uses the same shape as `.ci/config.yaml`. |
| `overrides.configFile` | `String` | no | `.ci/config.yaml` | Path of the YAML file to read. Removed from the map before merging. |

## Returns

The fully merged and validated config `Map`. Fails the build with every
problem listed if [`configValidate`](configValidate.md) finds any.

## Examples

**Default file:**

```yaml
# .ci/config.yaml
appName: orders-api
buildTool: python
imageRepo: ghcr.io/acme/orders-api
```

```groovy
def cfg = configLoad()

cfg.appName                   // → 'orders-api'
cfg.deployStrategy            // → 'gitops'        (derived)
cfg.quality.sonarProjectKey   // → 'orders-api'    (defaults to appName)
cfg.quality.trivyFailOn       // → ['HIGH', 'CRITICAL']
```

**Inline overrides and a different file:**

```groovy
def cfg = configLoad(
    configFile: '.ci/nightly.yaml',
    quality   : [minCoverage: 90, dependencyCheck: true],
    notify    : [on: 'always'],
)
```

**Infrastructure repo — derived values:**

```yaml
appName: platform-network
buildTool: terraform
```

```groovy
def cfg = configLoad()
cfg.containerize      // → false
cfg.deployStrategy    // → 'terraform'
cfg.quality.sbom      // → false
```

**Build log output:**

```
[INFO]  Loaded pipeline config from .ci/config.yaml
[WARN]  unknown config key 'quality.minCoverge' — did you mean 'minCoverage'? (in .ci/config.yaml)
[WARN]  extra.sonarSources is deprecated — move it to quality.sonarSources
```

**Invalid config — the build fails with every problem at once:**

```
Invalid pipeline config (.ci/config.yaml):
  - imageRepo is required when containerize is true
  - environments[1] (prod) requires approval but lists no approvers
```

## How it fits together

1. Reads the YAML file (if present) with `readYaml`; otherwise logs a warning and falls back to defaults + overrides only.
2. Restores `notify.on`: YAML 1.1 reads the bare key `on:` as boolean `true`, which used to make the setting silently ignored.
3. Logs a warning for every unknown or misspelled key via [`configUnknownKeys`](configUnknownKeys.md) (with a "did you mean" hint). Warnings never fail the build.
4. Merges [`configDefaults`](configDefaults.md) → the YAML → `overrides`, each step via [`configMerge`](configMerge.md).
5. Applies [`configDeprecatedKeys`](configDeprecatedKeys.md): an old `extra.*` key is copied to its new home with a deprecation warning, unless the new key is also set (the new key wins).
6. Merges [`configEnvDefaults`](configEnvDefaults.md) into each `environments:` entry, defaulting `namespace` to the environment's `name`, and defaults `quality.sonarProjectKey` to `appName`.
7. Derives `deployStrategy` (`gitops` for apps, the `buildTool` for infra) and forces `containerize`/`sbom`/`signImage` off for infrastructure repos (`buildTool` in [`configInfraTools`](../cloud/configInfraTools.md)).
8. Runs [`configValidate`](configValidate.md) and raises a single error listing every problem found, rather than failing on the first.

Called by [initPipeline](../ci-cd/initPipeline.md) at the start of every run.

## Source

[`vars/configLoad.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configLoad.groovy)
