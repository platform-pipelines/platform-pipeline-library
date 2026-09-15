# buildApp

Routes to the right build step for this repo's `buildTool`.

## Syntax

```groovy
buildApp(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; `cfg.buildTool` selects the build step. |

## Returns

Nothing. Fails the build for an unknown tool:
`No build step for buildTool 'rust'`.

| `buildTool` | Step | What it runs |
|---|---|---|
| `go` | [goBuild](../languages/goBuild.md) | `go mod download`, `go build ./...` |
| `python` | [pythonBuild](../languages/pythonBuild.md) | `pip install -r requirements.txt`, `python -m compileall` |
| `maven` | [mavenBuild](../languages/mavenBuild.md) | `mvn clean compile` |
| `gradle` | [gradleBuild](../languages/gradleBuild.md) | `gradle classes` |
| `npm` | [nodeBuild](../languages/nodeBuild.md) | `npm ci`, `npm run build --if-present` |
| `terraform` | [terraformBuild](../infrastructure/terraformBuild.md) | `terraform init`, `terraform validate` |
| `cloudformation` | [cfnBuild](../infrastructure/cfnBuild.md) | `aws cloudformation validate-template` |
| `docker-only` | [dockerOnlyBuild](../languages/dockerOnlyBuild.md) | nothing |

## Examples

```yaml
buildTool: gradle
```

```groovy
buildApp(cfg)      // → gradleBuild(cfg)
```

Inside a custom stage, in the right container:

```groovy
stage('Build') {
    agent { label 'linux' }
    steps {
        script {
            unstash 'source'
            inBuildContainer(cfg) { buildApp(cfg) }
        }
    }
}
```

## How it fits

Called from the `Build` stage of [standardPipeline](standardPipeline.md).

## Source

[`vars/buildApp.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/buildApp.groovy)
