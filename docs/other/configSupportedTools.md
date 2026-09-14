# configSupportedTools

Single source of truth for legal `buildTool` values. Adding a language or an
IaC type means adding it here and registering its lint/build/test steps.

## Syntax

```groovy
configSupportedTools()
```

## Parameters

None.

## Returns

`List<String>` of every `buildTool` value [`configValidate`](configValidate.md)
accepts:

| `buildTool` | Kind | Steps used |
|---|---|---|
| `maven` | application | `mavenLint` / `mavenBuild` / `mavenTest` / `mavenPackage` |
| `gradle` | application | `gradleLint` / `gradleBuild` / `gradleTest` / `gradlePackage` |
| `npm` | application | `nodeLint` / `nodeBuild` / `nodeTest` / `nodePackage` |
| `python` | application | `pythonLint` / `pythonBuild` / `pythonTest` / `pythonPackage` |
| `go` | application | `goLint` / `goBuild` / `goTest` / `goPackage` |
| `docker-only` | application | `dockerOnlyLint` (+ no-op build/test/package) |
| `terraform` | infrastructure | `terraformLint` / `terraformBuild` / `terraformTest` / `terraformPackage` |
| `cloudformation` | infrastructure | `cfnLint` / `cfnBuild` / `cfnTest` / `cfnPackage` |

## Examples

```groovy
configSupportedTools()
// → ['maven', 'gradle', 'npm', 'python', 'go', 'docker-only', 'terraform', 'cloudformation']

'npm' in configSupportedTools()      // → true
'node' in configSupportedTools()     // → false — Node repos use buildTool: npm
```

```yaml
# .ci/config.yaml
buildTool: npm
```

An unsupported value fails validation with:

```
buildTool 'node' unsupported (use: maven, gradle, npm, python, go, docker-only, terraform, cloudformation)
```

## How it fits

See [`configInfraTools`](../cloud/configInfraTools.md) for the subset of
these that are infrastructure rather than an application.

## Source

[`vars/configSupportedTools.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configSupportedTools.groovy)
