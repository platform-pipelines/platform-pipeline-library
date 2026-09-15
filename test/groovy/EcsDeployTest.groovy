import org.junit.Before
import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThatThrownBy

/**
 * deployEcs changes a running service. The two things that must hold: only
 * the image changes, and a rollout that never stabilises is rolled back to
 * the revision that was running before.
 */
class EcsDeployTest extends BaseTest {

    static final String PREVIOUS = 'arn:aws:ecs:eu-west-1:123456789012:task-definition/orders-api:7'
    static final String NEXT     = 'arn:aws:ecs:eu-west-1:123456789012:task-definition/orders-api:8'

    int waitStatus = 0

    Map describedTaskDefinition = [
        taskDefinitionArn   : PREVIOUS,
        revision            : 7,
        status              : 'ACTIVE',
        registeredAt        : '2026-09-01T10:00:00Z',
        registeredBy        : 'arn:aws:iam::123456789012:role/terraform',
        compatibilities     : ['EC2', 'FARGATE'],
        requiresAttributes  : [[name: 'com.amazonaws.ecs.capability.logging-driver.awslogs']],
        family              : 'orders-api',
        cpu                 : '256',
        memory              : '512',
        executionRoleArn    : 'arn:aws:iam::123456789012:role/orders-api-exec',
        containerDefinitions: [
            [name: 'orders-api', image: '123456789012.dkr.ecr.eu-west-1.amazonaws.com/orders-api:1.3.0', essential: true],
            [name: 'otel', image: 'public.ecr.aws/aws-observability/aws-otel-collector:v0.40.0'],
        ],
    ]

    Map cfg = [
        appName : 'orders-api',
        imageRepo: '123456789012.dkr.ecr.eu-west-1.amazonaws.com/orders-api',
        infra   : [region: 'eu-west-1', awsCredentialsId: 'aws-credentials'],
    ]

    Map prod = [name: 'prod', ecsCluster: 'platform-prod', ecsService: 'orders-api']

    @Override
    @Before
    void setUp() {
        super.setUp()
        binding.getVariable('env').IMAGE_TAG = '1.4.0'
        helper.registerAllowedMethod('readJSON', [Map]) { Map m -> describedTaskDefinition }
    }

    @Override
    String stubStdout(String script) {
        if (script.contains('describe-services')) { return PREVIOUS + '\n' }
        if (script.contains('register-task-definition')) { return NEXT + '\n' }
        return super.stubStdout(script)
    }

    @Override
    int stubStatus(String script) {
        return script.contains('wait services-stable') ? waitStatus : 0
    }

    @Test
    void 'task definition keeps everything but the image and drops read-only fields'() {
        def next = step('ecsTaskDefinitionForImage').call(describedTaskDefinition, 'orders-api', 'repo/orders-api:1.4.0')

        assertThat(next.keySet()).doesNotContain('taskDefinitionArn', 'revision', 'status', 'registeredAt',
                                                 'registeredBy', 'compatibilities', 'requiresAttributes')
        assertThat(next.family).isEqualTo('orders-api')
        assertThat(next.executionRoleArn).isEqualTo('arn:aws:iam::123456789012:role/orders-api-exec')
        assertThat(next.containerDefinitions[0].image).isEqualTo('repo/orders-api:1.4.0')
        assertThat(next.containerDefinitions[0].essential).isEqualTo(true)
        assertThat(next.containerDefinitions[1].image).contains('aws-otel-collector:v0.40.0')
        // the input is not mutated
        assertThat(describedTaskDefinition.containerDefinitions[0].image).endsWith(':1.3.0')
    }

    @Test
    void 'task definition returns null for an unknown container'() {
        assertThat(step('ecsTaskDefinitionForImage').call(describedTaskDefinition, 'nope', 'x:1')).isNull()
    }

    @Test
    void 'registers a new revision and rolls the service onto it'() {
        step('deployEcs').call(cfg, prod)

        assertThat(ranMatching(/register-task-definition --cli-input-json file:\/\/ecs-taskdef\.json/)).isTrue()
        assertThat(ranMatching(/update-service --cluster 'platform-prod' --service 'orders-api' --task-definition '.*orders-api:8'/)).isTrue()
        assertThat(ranMatching(/wait services-stable/)).isTrue()
        assertThat(ranMatching(/task-definition '.*orders-api:7' >/)).isFalse()
        assertThat(existingFiles['ecs-taskdef.json']).contains('orders-api:1.4.0').doesNotContain('taskDefinitionArn')
        // credentials for the environment's region
        assertThat(envVars*.toString()).contains('AWS_REGION=eu-west-1')
    }

    @Test
    void 'rolls back to the previous revision when the service never stabilises'() {
        waitStatus = 255

        assertThatThrownBy { step('deployEcs').call(cfg, prod) }
            .hasMessageContaining('rolled back to ' + PREVIOUS)

        assertThat(ranMatching(/update-service .* --task-definition '.*orders-api:7' >/)).isTrue()
    }

    @Test
    void 'names the containers when ecsContainer does not match'() {
        assertThatThrownBy { step('deployEcs').call(cfg, prod + [ecsContainer: 'web']) }
            .hasMessageContaining("no container named 'web'")
            .hasMessageContaining('orders-api, otel')

        assertThat(ranMatching(/update-service/)).isFalse()
    }

    @Test
    void 'ecs strategy requires a cluster and service per environment'() {
        configFile('''
appName: orders-api
buildTool: go
imageRepo: 123456789012.dkr.ecr.eu-west-1.amazonaws.com/orders-api
deployStrategy: ecs
environments:
  - name: dev
    ecsCluster: platform-dev
''')
        assertThatThrownBy { step('configLoad').call() }
            .hasMessageContaining('environments[0].ecsService is required for deployStrategy: ecs')
    }

    @Test
    void 'ecs strategy does not need a gitops repo or manifest path'() {
        configFile('''
appName: orders-api
buildTool: go
imageRepo: 123456789012.dkr.ecr.eu-west-1.amazonaws.com/orders-api
deployStrategy: ecs
environments:
  - name: dev
    ecsCluster: platform-dev
    ecsService: orders-api
''')
        assertThat(step('configLoad').call().deployStrategy).isEqualTo('ecs')
    }
}
