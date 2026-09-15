# ecsTaskDefinitionForImage

Turns a described ECS task definition into `register-task-definition` input
with one container's image replaced. Read-only fields are dropped; everything
else carries over.

## Syntax

```groovy
Map ecsTaskDefinitionForImage(Map taskDefinition, String containerName, String image)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `taskDefinition` | `Map` | yes | — | The `taskDefinition` object from `describe-task-definition`. |
| `containerName` | `String` | yes | — | Container whose image is replaced. |
| `image` | `String` | yes | — | Full image reference. |

## Returns

A new `Map` (the input is not modified), or `null` when no container has that
name. Removed: `taskDefinitionArn`, `revision`, `status`, `requiresAttributes`,
`compatibilities`, `registeredAt`, `registeredBy`, `deregisteredAt`.

## Examples

```groovy
def next = ecsTaskDefinitionForImage(current, 'payments-api', "${cfg.imageRepo}:1.4.0")
writeJSON file: 'ecs-taskdef.json', json: next
```

## Source

[`vars/ecsTaskDefinitionForImage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/ecsTaskDefinitionForImage.groovy)
