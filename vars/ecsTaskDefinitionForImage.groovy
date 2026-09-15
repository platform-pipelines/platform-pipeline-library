import com.cloudbees.groovy.cps.NonCPS

// Turns a described ECS task definition into register-task-definition input
// with one container's image replaced.
//
// describe-task-definition returns read-only fields (ARN, revision, status,
// registeredAt, ...) that register-task-definition rejects, so they are
// dropped. Everything else — CPU/memory, roles, env, secrets, logging,
// sidecars — carries over unchanged, which is the point: the pipeline changes
// the image and nothing else, and the task definition itself stays owned by
// Terraform or CloudFormation.
//
// Usage:
//   def next = ecsTaskDefinitionForImage(current, 'orders-api', '1234.dkr.ecr.eu-west-1.amazonaws.com/orders-api:1.4.0')
// Params: taskDefinition (Map) - the `taskDefinition` object from `aws ecs describe-task-definition`
//         containerName (String) - name of the container whose image is replaced
//         image (String) - full image reference to set
// Returns: a new Map ready for --cli-input-json, or null when no container has that name
@NonCPS
def call(Map taskDefinition, String containerName, String image) {
    def readOnly = ['taskDefinitionArn', 'revision', 'status', 'requiresAttributes',
                    'compatibilities', 'registeredAt', 'registeredBy', 'deregisteredAt']

    def out = [:]
    taskDefinition.each { k, v ->
        if (!(k.toString() in readOnly) && v != null) { out[k.toString()] = v }
    }

    def containers = (out.containerDefinitions ?: []).collect { c -> [:] + (c as Map) }
    def target = containers.find { it.name == containerName }
    if (target == null) {
        return null
    }

    target.image = image
    out.containerDefinitions = containers
    return out
}
