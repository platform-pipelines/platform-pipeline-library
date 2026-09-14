import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat

/**
 * Examples are what people copy. Every examples/<name>/config.yaml must load
 * cleanly — no validation errors, no unknown or deprecated keys — so an
 * example can never quietly drift away from what the library accepts.
 */
class ExamplesConfigTest extends BaseTest {

    @Test
    void 'every example config loads without errors or warnings'() {
        List<File> configs = new File('examples').listFiles()
            .findAll { it.isDirectory() }
            .collect { new File(it, 'config.yaml') }
            .findAll { it.exists() }
            .sort { it.path }

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
    void 'every example has a Jenkinsfile and a README'() {
        new File('examples').listFiles().findAll { it.isDirectory() }.each { dir ->
            assertThat(new File(dir, 'Jenkinsfile')).as("${dir}/Jenkinsfile").exists()
            assertThat(new File(dir, 'README.md')).as("${dir}/README.md").exists()
        }
    }
}
