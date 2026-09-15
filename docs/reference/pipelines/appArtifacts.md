# appArtifacts

Build output worth archiving and publishing, or `null`.

## Syntax

```groovy
appArtifacts(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; only `cfg.buildTool` is read. |

## Returns

An Ant-style glob `String` for `cfg.buildTool`, or `null`:

| `buildTool` | Glob | Produced by |
|---|---|---|
| `go` | `dist/*` | [goPackage](../languages/goPackage.md) |
| `python` | `dist/*` | [pythonPackage](../languages/pythonPackage.md) |
| `maven` | `target/*.jar` | [mavenPackage](../languages/mavenPackage.md) |
| `gradle` | `build/libs/*.jar` | [gradlePackage](../languages/gradlePackage.md) |
| `npm` | `*.tgz` | [nodePackage](../languages/nodePackage.md) |
| `terraform` | `tfplan-*.txt` | [terraformPlan](../infrastructure/terraformPlan.md) |
| `cloudformation` | `packaged-template.yaml` | [cfnPackage](../infrastructure/cfnPackage.md) |
| `docker-only` | `null` | — the image is the artifact |

## Examples

```groovy
appArtifacts([buildTool: 'gradle'])        // → 'build/libs/*.jar'
appArtifacts([buildTool: 'docker-only'])   // → null
```

```groovy
def glob = appArtifacts(cfg)
if (glob) {
    archiveArtifacts artifacts: glob, allowEmptyArchive: true, fingerprint: true
}
```

## How it fits

Used by [standardPipeline](standardPipeline.md)'s `Package` stage and by
[publishArtifact](../deploy/publishArtifact.md).

## Source

[`vars/appArtifacts.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/appArtifacts.groovy)
