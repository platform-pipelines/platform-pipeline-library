import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat

/**
 * Examples are what people copy. Every examples/<name>/.ci/config.yaml must
 * load cleanly — no validation errors, no unknown or deprecated keys — so an
 * example can never quietly drift away from what the library accepts.
 *
 * Each example is laid out as a repo root (.ci/config.yaml, Jenkinsfile,
 * source), so copying the directory gives a repo the pipeline can build.
 */
class ExamplesConfigTest extends BaseTest {

    private List<File> exampleDirs() {
        new File('examples').listFiles().findAll { it.isDirectory() }.sort { it.name }
    }

    @Test
    void 'every example config loads without errors or warnings'() {
        List<File> configs = exampleDirs().collect { new File(it, '.ci/config.yaml') }

        configs.each { assertThat(it).as('example config').exists() }
        assertThat(configs).as('example configs').isNotEmpty()

        configs.each { File file ->
            List<String> warnings = []
            helper.registerAllowedMethod('echo', [String]) { String m ->
                if (m.startsWith('[WARN]')) { warnings << m }
            }
            existingFiles.clear()
            configFile(file.text)

            def cfg = step('configLoad').call()

            assertThat(cfg.appName).as("appName in ${file}").isNotNull()
            assertThat(warnings).as("warnings for ${file}").isEmpty()
        }
    }

    @Test
    void 'every extra Jenkinsfile calls a library entrypoint'() {
        def entrypoints = ['standardPipeline', 'cdPipeline', 'terraformDriftPipeline']
        exampleDirs().each { dir ->
            dir.listFiles().findAll { it.name.startsWith('Jenkinsfile') }.each { file ->
                assertThat(file.text).as("${file}").contains("@Library('platform-pipeline@")
                assertThat(entrypoints.any { file.text.contains("${it}(") }).as("${file} calls ${entrypoints}").isTrue()
                entrypoints.findAll { file.text.contains("${it}(") }.each {
                    assertThat(new File("vars/${it}.groovy")).exists()
                }
            }
        }
    }

    @Test
    void 'every example has a Jenkinsfile and a README'() {
        exampleDirs().each { dir ->
            assertThat(new File(dir, 'Jenkinsfile')).as("${dir}/Jenkinsfile").exists()
            assertThat(new File(dir, 'README.md')).as("${dir}/README.md").exists()
        }
    }
}
