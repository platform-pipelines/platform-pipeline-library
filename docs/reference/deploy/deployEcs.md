# deployEcs

Application deploy to Amazon ECS: register a task definition revision with the
new image, roll the service onto it, wait until stable, and roll back to the
previous revision if it never gets there.

## Syntax

```groovy
deployEcs(Map cfg, Map envCfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |
| `envCfg` | `Map` | yes | — | Target environment. |

### Config keys read

| Key | Default | Sample value | Used for |
|---|---|---|---|
| `deployStrategy` | `gitops` | `ecs` | routes here via [deployToEnvironment](deployToEnvironment.md) |
| `imageRepo` | — | `1111….dkr.ecr.eu-west-1.amazonaws.com/payments-api` | new image `<imageRepo>:<IMAGE_TAG>` |
| `infra.region` | `us-east-1` | `eu-west-1` | AWS region |
| `environments[].ecsCluster` | — (required) | `platform-prod` | cluster |
| `environments[].ecsService` | — (required) | `payments-api` | service |
| `environments[].ecsContainer` | `appName` | `api` | container whose image changes |
| `environments[].assumeRole` | `infra.assumeRole` | `arn:aws:iam::444455556666:role/JenkinsDeploy` | [withAwsCredentials](../infrastructure/withAwsCredentials.md) |

## Returns

Nothing. Errors, after rolling back, if the service is not stable within
`aws ecs wait services-stable` (10 minutes).

## Examples

```groovy
deployEcs(cfg, cfg.environments[0])
```

| Step | Command |
|---|---|
| 1 | `aws ecs describe-services` → current revision `payments-api:7` |
| 2 | `describe-task-definition` → [ecsTaskDefinitionForImage](ecsTaskDefinitionForImage.md) → `ecs-taskdef.json` |
| 3 | `register-task-definition` → `payments-api:8` |
| 4 | `update-service --task-definition payments-api:8` |
| 5 | `wait services-stable` |
| 5a (timeout) | `update-service --task-definition payments-api:7`, wait, audit `deploy.rollback`, fail |

Only the image changes; the task definition's shape stays owned by Terraform
or CloudFormation. Give the service `deployment_circuit_breaker { rollback = true }`
and `lifecycle { ignore_changes = [task_definition] }` — see
[examples/terraform-aws-platform](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/terraform-aws-platform).

## Source

[`vars/deployEcs.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/deployEcs.groovy)
