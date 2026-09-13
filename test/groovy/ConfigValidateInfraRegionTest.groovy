import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat

/**
 * cfnBuild/cfnPackage dispatch on buildTool == 'cloudformation' independently
 * of deployStrategy (buildApp.groovy / packageApp.groovy), and both call
 * withAwsCredentials, which needs infra.region. The region check must not be
 * gated on deployStrategy alone, or an explicit deployStrategy override could
 * skip it.
 */
class ConfigValidateInfraRegionTest extends BaseTest {

    private Map baseCfg(Map overrides = [:]) {
        [
            appName: 'billing',
            buildTool: 'maven',
            deployStrategy: 'gitops',
            infra: [:],
            quality: [:],
            notify: [on: 'change'],
            environments: [],
        ] + overrides
    }

    @Test
    void 'requires infra region when deployStrategy is cloudformation'() {
        def errors = step('configValidate').call(baseCfg(deployStrategy: 'cloudformation'))
        assertThat(errors).anyMatch { it.contains('infra.region is required for cloudformation') }
    }

    @Test
    void 'requires infra region when buildTool is cloudformation even if deployStrategy is overridden'() {
        def errors = step('configValidate').call(baseCfg(buildTool: 'cloudformation', deployStrategy: 'terraform'))
        assertThat(errors).anyMatch { it.contains('infra.region is required for cloudformation') }
    }

    @Test
    void 'passes once infra region is supplied'() {
        def errors = step('configValidate').call(baseCfg(buildTool: 'cloudformation', infra: [region: 'us-east-1']))
        assertThat(errors).noneMatch { it.contains('infra.region') }
    }
}
