# Configuration

Loading, merging and validating `.ci/config.yaml`. For the full list of keys,
see the [Configuration guide](../../guides/configuration.md).

- [configLoad](configLoad.md): reads `.ci/config.yaml`, merges it over [configDefaults](configDefaults.md), validates it, and returns a plain `Map`.
- [configDefaults](configDefaults.md), [configEnvDefaults](configEnvDefaults.md): the baseline every repo inherits, and the config schema.
- [configMerge](configMerge.md): recursive map merge (right side wins).
- [configValidate](configValidate.md): collects every config problem in one pass instead of failing on the first.
- [configUnknownKeys](configUnknownKeys.md), [configClosestKey](configClosestKey.md): warn about typos, with a "did you mean" hint.
- [configDeprecatedKeys](configDeprecatedKeys.md): keys that moved and still work with a warning.
- [configSupportedTools](configSupportedTools.md), [configImageBuilders](configImageBuilders.md), [configDeployStrategies](configDeployStrategies.md): allowlists for `buildTool`, `imageBuilder` and `deployStrategy`. [configInfraTools](../infrastructure/configInfraTools.md) holds the infra subset.
- [configEnvironmentsFor](configEnvironmentsFor.md), [configGlobToRegex](configGlobToRegex.md): which environments a given branch reaches.

```groovy
def cfg  = configLoad()                                  // merged + validated Map
def envs = configEnvironmentsFor(cfg, env.BRANCH_NAME)   // e.g. [[name: 'dev', ...], [name: 'prod', ...]]
```
