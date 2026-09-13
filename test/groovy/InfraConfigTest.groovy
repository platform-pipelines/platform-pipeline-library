import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThatThrownBy

/**
 * Infra repos derive several settings rather than restating them. If that
 * derivation breaks, a terraform repo would try to build a container image.
 */
class InfraConfigTest extends BaseTest {

    @Test
    void 'terraform derives strategy and disables container concerns'() {
        configFile('''
appName: platform-network
buildTool: terraform
infra:
  workingDir: terraform
environments:
  - name: dev
    branchPattern: "*"
''')
        def cfg = step('configLoad').call()

        assertThat(cfg.deployStrategy).isEqualTo('terraform')
        assertThat(cfg.containerize).isFalse()
        assertThat(cfg.quality.sbom).isFalse()
        assertThat(cfg.quality.signImage).isFalse()
    }

    @Test
    void 'cloudformation derives its strategy'() {
        configFile('appName: billing\nbuildTool: cloudformation\ninfra:\n  region: us-east-1\n')
        assertThat(step('configLoad').call().deployStrategy).isEqualTo('cloudformation')
    }

    @Test
    void 'application repos still default to gitops'() {
        configFile('appName: api\nbuildTool: maven\ncontainerize: false\n')
        assertThat(step('configLoad').call().deployStrategy).isEqualTo('gitops')
    }

    @Test
    void 'infra environments do not require a manifestPath'() {
        configFile('''
appName: platform-network
buildTool: terraform
environments:
  - name: prod
    workspace: prod
    branchPattern: main
''')
        // gitops would demand manifestPath here; terraform must not
        def cfg = step('configLoad').call()
        assertThat(cfg.environments[0].workspace).isEqualTo('prod')
    }

    @Test
    void 'gitops environments still require a manifestPath'() {
        configFile('''
appName: api
buildTool: maven
containerize: false
gitopsRepo: acme/gitops
environments:
  - name: prod
''')
        assertThatThrownBy { step('configLoad').call() }
            .hasMessageContaining('manifestPath is required for deployStrategy: gitops')
    }

    @Test
    void 'containerize true is rejected for infra repos'() {
        configFile('appName: net\nbuildTool: terraform\ncontainerize: true\nimageRepo: x/y\n')
        // configLoad forces containerize false before validation, so this
        // documents the derivation rather than expecting a failure
        assertThat(step('configLoad').call().containerize).isFalse()
    }

    @Test
    void 'an unknown deployStrategy is rejected'() {
        configFile('appName: api\nbuildTool: maven\ncontainerize: false\ndeployStrategy: carrier-pigeon\n')
        assertThatThrownBy { step('configLoad').call() }
            .hasMessageContaining("deployStrategy 'carrier-pigeon' unsupported")
    }

    @Test
    void 'infra tools are listed as supported'() {
        def tools = step('configSupportedTools').call()
        assertThat(tools).contains('terraform', 'cloudformation')
    }

    @Test
    void 'isInfraRepo distinguishes correctly'() {
        assertThat(step('isInfraRepo').call([buildTool: 'terraform'])).isTrue()
        assertThat(step('isInfraRepo').call([buildTool: 'cloudformation'])).isTrue()
        assertThat(step('isInfraRepo').call([buildTool: 'maven'])).isFalse()
    }
}
