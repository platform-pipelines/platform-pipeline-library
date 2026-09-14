# isInfraRepo

True when this repo describes infrastructure rather than an application.

## Syntax

```groovy
isInfraRepo(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; only `cfg.buildTool` is read. |

## Returns

`true` if `cfg.buildTool` is in [configInfraTools](configInfraTools.md)
(`terraform`, `cloudformation`), otherwise `false`.

## Examples

```groovy
isInfraRepo([buildTool: 'terraform'])        // → true
isInfraRepo([buildTool: 'cloudformation'])   // → true
isInfraRepo([buildTool: 'python'])           // → false
```

Branching a custom stage:

```groovy
stage('Publish') {
    steps {
        script {
            if (isInfraRepo(cfg)) {
                logInfo 'Infra repo — the plan is the artifact'
            } else {
                publishArtifact(cfg)
            }
        }
    }
}
```

## How it fits

Used by [qualityChecks](../ci-cd/qualityChecks.md) (IaC scan instead of a
dependency scan) and [standardPipeline](../ci-cd/standardPipeline.md) (skip
artifact publishing).

## Source

[`vars/isInfraRepo.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/isInfraRepo.groovy)
