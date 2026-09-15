# configEnvironmentsFor

Which environments the given branch is permitted to reach.

## Syntax

```groovy
configEnvironmentsFor(Map cfg, String branch)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; reads `cfg.environments`. |
| `branch` | `String` | yes | — | Branch name to match against each environment's `branchPattern`, usually `env.BRANCH_NAME`. |

### Config keys read

| Key | Default | Sample value |
|---|---|---|
| `environments[].branchPattern` | `main` | `"*"`, `main`, `release/*` |

## Returns

A `List` of environment config `Map`s the branch may deploy to, in declaration
order. The list is empty when nothing matches, or when `branch` is `null`/empty.

## Examples

Given:

```yaml
environments:
  - name: dev
    branchPattern: "*"
  - name: staging
    branchPattern: release/*
  - name: prod
    branchPattern: main
```

```groovy
configEnvironmentsFor(cfg, 'main')*.name              // → ['dev', 'prod']
configEnvironmentsFor(cfg, 'release/1.4')*.name       // → ['dev', 'staging']
configEnvironmentsFor(cfg, 'feature/login')*.name     // → ['dev']
configEnvironmentsFor(cfg, null)                      // → []
```

Typical use:

```groovy
configEnvironmentsFor(cfg, env.BRANCH_NAME).each { envCfg ->
    deployToEnvironment(cfg, envCfg)
}
```

## How it fits

Matches each environment's `branchPattern` by converting it to a regex via
[`configGlobToRegex`](configGlobToRegex.md). Used by
[standardPipeline](../pipelines/standardPipeline.md)'s Deploy stage and by
[initPipeline](../pipelines/initPipeline.md)'s build summary line.

## Source

[`vars/configEnvironmentsFor.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configEnvironmentsFor.groovy)
