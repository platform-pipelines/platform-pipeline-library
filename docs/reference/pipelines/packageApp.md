# packageApp

Routes to the right package step for this repo's `buildTool`.

## Syntax

```groovy
packageApp(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; `cfg.buildTool` selects the package step. |

## Returns

Nothing. Fails the build for an unknown tool:
`No package step for buildTool 'rust'`.

| `buildTool` | Step | Output |
|---|---|---|
| `go` | [goPackage](../languages/goPackage.md) | `dist/<appName>` |
| `python` | [pythonPackage](../languages/pythonPackage.md) | `dist/*.whl`, `dist/*.tar.gz` |
| `maven` | [mavenPackage](../languages/mavenPackage.md) | `target/*.jar` |
| `gradle` | [gradlePackage](../languages/gradlePackage.md) | `build/libs/*.jar` |
| `npm` | [nodePackage](../languages/nodePackage.md) | `<name>-<version>.tgz` |
| `terraform` | [terraformPackage](../infrastructure/terraformPackage.md) | nothing (plan is made at deploy) |
| `cloudformation` | [cfnPackage](../infrastructure/cfnPackage.md) | `packaged-template.yaml` (when `infra.artifactBucket` is set) |
| `docker-only` | [dockerOnlyPackage](../languages/dockerOnlyPackage.md) | nothing (image is the artifact) |

## Examples

```yaml
buildTool: python
```

```groovy
packageApp(cfg)                     // → pythonPackage(cfg)
```

Packaging, then archiving and publishing the result — what the `Package`
stage does:

```groovy
inBuildContainer(cfg) { packageApp(cfg) }

def artifacts = appArtifacts(cfg)                 // 'dist/*'
if (artifacts) {
    archiveArtifacts artifacts: artifacts, allowEmptyArchive: true, fingerprint: true
}
if (!isInfraRepo(cfg)) {
    publishArtifact(cfg)
}
```

## How it fits

Called from the `Package` stage of [standardPipeline](standardPipeline.md).

## Source

[`vars/packageApp.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/packageApp.groovy)
