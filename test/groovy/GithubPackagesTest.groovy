import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThatThrownBy

/**
 * imageRegistry: github and publish.githubPackages keep the image and the
 * build output in GitHub Packages, under the same repo as the code. The path
 * comes from the checkout, so a wrong owner/repo can never be configured.
 */
class GithubPackagesTest extends BaseTest {

    private static final String APP = '''
appName: payments-api
buildTool: python
'''

    private static final Map WHEEL = [path: 'dist/payments_api-1.4.0-py3-none-any.whl', name: 'payments_api-1.4.0-py3-none-any.whl']

    private void publish(Map overrides) {
        binding.getVariable('env').IMAGE_TAG = '1.4.0'
        helper.registerAllowedMethod('findFiles', [Map]) { [WHEEL] }
        Map cfg = [appName: 'payments-api', buildTool: 'python', imageRepo: 'ghcr.io/acme/payments-api',
                   infra: [awsCredentialsId: 'aws-credentials'], publish: [nexusRepo: null, githubPackages: false]]
        step('publishArtifact').call(step('configMerge').call(cfg, overrides))
    }

    @Test
    void 'imageRegistry github derives imageRepo from the repo being built'() {
        configFile(APP + 'imageRegistry: github\n')
        def cfg = step('configLoad').call()

        assertThat(cfg.imageRepo).isEqualTo('ghcr.io/acme/payments-api')
    }

    @Test
    void 'an explicit ghcr imageRepo wins over the derived one'() {
        configFile(APP + 'imageRegistry: github\nimageRepo: ghcr.io/acme/payments-api/worker\n')
        def cfg = step('configLoad').call()

        assertThat(cfg.imageRepo).isEqualTo('ghcr.io/acme/payments-api/worker')
    }

    @Test
    void 'the path is lowercased because GHCR rejects upper case'() {
        binding.getVariable('env').GIT_URL = 'git@github.com:Acme/Payments-API.git'

        assertThat(step('githubPackagesRepo').call()).isEqualTo('ghcr.io/acme/payments-api')
        assertThat(step('githubPackagesRepo').call('Payments-API-artifacts'))
            .isEqualTo('ghcr.io/acme/payments-api/payments-api-artifacts')
    }

    @Test
    void 'rejects a package name GHCR would refuse'() {
        assertThatThrownBy { step('githubPackagesRepo').call('bad name;rm') }
            .hasMessageContaining('is not a valid package name')
    }

    @Test
    void 'rejects an unknown imageRegistry and a contradicting imageRepo'() {
        configFile(APP + 'imageRegistry: quay\n')
        assertThatThrownBy { step('configLoad').call() }
            .hasMessageContaining("imageRegistry 'quay' unsupported")

        configFile(APP + 'imageRegistry: github\nimageRepo: registry.local:5000/acme/api\n')
        assertThatThrownBy { step('configLoad').call() }
            .hasMessageContaining('imageRegistry: github pushes to ghcr.io')
    }

    @Test
    void 'publishes artifacts to GHCR under the same repo, linked by the source annotation'() {
        publish([publish: [githubPackages: true]])

        def push = shellCommands.find { it.contains('oras push') }
        assertThat(push).isNotNull()
        assertThat(push).contains('ghcr.io/acme/payments-api/payments-api-artifacts:1.4.0')
        assertThat(push).contains('org.opencontainers.image.source=https://github.com/acme/payments-api')
        assertThat(push).contains(WHEEL.path)
        assertThat(envVars*.toString()).contains('REG_HOST=ghcr.io')
        assertThat(ranMatching(/--upload-file/)).isFalse()
    }

    @Test
    void 'artifacts go to GitHub even when the image goes to ECR'() {
        publish([imageRepo: '123456789012.dkr.ecr.eu-west-1.amazonaws.com/payments-api', publish: [githubPackages: true]])

        assertThat(envVars*.toString()).contains('REG_HOST=ghcr.io')
        assertThat(ranMatching(/aws ecr get-login-password/)).isFalse()
    }

    @Test
    void 'Nexus and GitHub Packages can both be on'() {
        publish([publish: [nexusRepo: 'pypi-internal', githubPackages: true]])

        assertThat(ranMatching(/--upload-file/)).isTrue()
        assertThat(ranMatching(/oras push/)).isTrue()
    }

    @Test
    void 'nothing is published when no store is configured'() {
        publish([:])

        assertThat(ranMatching(/--upload-file|oras push/)).isFalse()
    }
}
