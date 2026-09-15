import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat

/**
 * Registry auth used to be hardcoded to ghcr.io, so any other registry got a
 * config with credentials for the wrong host and the push failed as "denied".
 * It was also written to /kaniko/.docker on the agent, where the kaniko
 * container never saw it.
 */
class KanikoDockerConfigTest extends BaseTest {

    private String hostFor(String imageRepo) {
        envVars.clear()
        step('kanikoDockerConfig').call([imageRepo: imageRepo, infra: [awsCredentialsId: 'aws-credentials']])
        return envVars*.toString().find { it.startsWith('REG_HOST=') } - 'REG_HOST='
    }

    @Test
    void 'uses the registry host from imageRepo'() {
        assertThat(hostFor('ghcr.io/acme/api')).isEqualTo('ghcr.io')
        assertThat(hostFor('123456789012.dkr.ecr.eu-west-1.amazonaws.com/api')).isEqualTo('123456789012.dkr.ecr.eu-west-1.amazonaws.com')
    }

    @Test
    void 'keeps the port for a self-hosted registry'() {
        assertThat(hostFor('registry.local:5000/acme/api')).isEqualTo('registry.local:5000')
        assertThat(hostFor('localhost/acme/api')).isEqualTo('localhost')
    }

    @Test
    void 'falls back to Docker Hub when the first segment is not a host'() {
        assertThat(hostFor('acme/api')).isEqualTo('https://index.docker.io/v1/')
    }

    @Test
    void 'ECR gets a login token in the registry region instead of a static credential'() {
        hostFor('123456789012.dkr.ecr.eu-west-1.amazonaws.com/api')

        assertThat(ranMatching(/aws ecr get-login-password --region eu-west-1/)).isTrue()
        assertThat(envVars*.toString()).contains('AWS_REGION=eu-west-1', 'REG_USER=AWS')
    }

    @Test
    void 'the config is written outside the workspace build context'() {
        binding.getVariable('env').WORKSPACE = '/agent/workspace/api'
        def dir = step('kanikoDockerConfig').call([imageRepo: 'ghcr.io/acme/api'])

        assertThat(dir).isEqualTo('/agent/workspace/api@tmp/registry-auth')
        assertThat(shellCommands).noneMatch { it.contains('/kaniko/.docker') }
    }
}
