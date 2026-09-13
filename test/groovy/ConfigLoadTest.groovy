import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThatThrownBy

class ConfigLoadTest extends BaseTest {

    @Test
    void 'applies defaults when the config file is minimal'() {
        configFile('''
appName: demo
buildTool: npm
containerize: false
''')
        def cfg = step('configLoad').call()

        assertThat(cfg.appName).isEqualTo('demo')
        assertThat(cfg.quality.sonar as boolean).isTrue()
        assertThat(cfg.quality.trivyFailOn).containsExactly('HIGH', 'CRITICAL')
        assertThat(cfg.lint.enabled as boolean).isTrue()
        assertThat(cfg.notify.on).isEqualTo('change')
    }

    @Test
    void 'inline overrides beat the yaml file'() {
        configFile('''
appName: demo
buildTool: npm
containerize: false
quality:
  minCoverage: 50
''')
        def cfg = step('configLoad').call([quality: [minCoverage: 90]])

        assertThat(cfg.quality.minCoverage).isEqualTo(90)
        // sibling keys survive the merge rather than being wiped
        assertThat(cfg.quality.sonar as boolean).isTrue()
    }

    @Test
    void 'rejects an unsupported build tool'() {
        configFile('appName: demo\nbuildTool: cobol\ncontainerize: false\n')
        assertThatThrownBy { step('configLoad').call() }
            .hasMessageContaining("buildTool 'cobol' unsupported")
    }

    @Test
    void 'reports every problem at once'() {
        configFile('containerize: true\n')
        assertThatThrownBy { step('configLoad').call() }
            .hasMessageContaining('appName is required')
            .hasMessageContaining('buildTool is required')
            .hasMessageContaining('imageRepo is required')
    }

    @Test
    void 'rejects duplicate environment names'() {
        configFile('''
appName: demo
buildTool: maven
containerize: false
gitopsRepo: acme/gitops
environments:
  - name: prod
    manifestPath: a.yaml
  - name: prod
    manifestPath: b.yaml
''')
        assertThatThrownBy { step('configLoad').call() }
            .hasMessageContaining("environment 'prod' is declared more than once")
    }

    @Test
    void 'namespace defaults to the environment name'() {
        configFile('''
appName: demo
buildTool: maven
containerize: false
gitopsRepo: acme/gitops
environments:
  - name: staging
    manifestPath: s.yaml
''')
        def cfg = step('configLoad').call()
        assertThat(cfg.environments[0].namespace).isEqualTo('staging')
    }
}
