import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat

/**
 * Every supported build tool must answer every metadata question, or a stage
 * silently does nothing. This walks the supported list rather than hardcoding
 * it, so adding a language to configSupportedTools forces the gaps to surface.
 */
class AppMetadataTest extends BaseTest {

    private List tools() { step('configSupportedTools').call() }

    @Test
    void 'every supported tool has a container image'() {
        tools().each { tool ->
            def image = step('appToolImage').call([buildTool: tool, runtimeVersion: null])
            assertThat(image).as("image for ${tool}").isNotNull().isNotEmpty()
        }
    }

    @Test
    void 'every supported tool answers the report questions without throwing'() {
        tools().each { tool ->
            def cfg = [buildTool: tool]
            step('appTestReport').call(cfg)
            step('appCoverageFile').call(cfg)
            step('appArtifacts').call(cfg)
            step('appCacheDir').call(cfg)
            step('appLintReport').call(cfg)
            assertThat(step('appSonarProps').call(cfg)).as("sonar props for ${tool}").isNotNull()
        }
    }

    @Test
    void 'runtimeVersion is honoured when supplied'() {
        assertThat(step('appToolImage').call([buildTool: 'go', runtimeVersion: '1.23']))
            .isEqualTo('golang:1.23')
        assertThat(step('appToolImage').call([buildTool: 'npm', runtimeVersion: '22']))
            .isEqualTo('node:22-alpine')
    }

    @Test
    void 'docker-only produces no artifacts or reports'() {
        def cfg = [buildTool: 'docker-only']
        assertThat(step('appTestReport').call(cfg)).isNull()
        assertThat(step('appArtifacts').call(cfg)).isNull()
    }
}
