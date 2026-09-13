import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThatThrownBy

/**
 * The dispatchers are trivial, but a typo in one means a whole language
 * silently skips linting, so each route is asserted.
 */
class LintDispatchTest extends BaseTest {

    private Map cfgFor(String tool) {
        [buildTool: tool, appName: 'demo', dockerfile: 'Dockerfile',
         lint: [enabled: true, failOnError: true, autoFormat: false]]
    }

    @Test
    void 'go lint runs gofmt and vet'() {
        step('goLint').call(cfgFor('go'))
        assertThat(ranMatching(/gofmt -l/)).isTrue()
        assertThat(ranMatching(/go vet/)).isTrue()
        assertThat(ranMatching(/golangci-lint run/)).isTrue()
    }

    @Test
    void 'python lint runs ruff check and format check'() {
        step('pythonLint').call(cfgFor('python'))
        assertThat(ranMatching(/ruff check \./)).isTrue()
        assertThat(ranMatching(/ruff format --check/)).isTrue()
    }

    @Test
    void 'maven lint runs checkstyle and spotbugs'() {
        step('mavenLint').call(cfgFor('maven'))
        assertThat(ranMatching(/checkstyle:check/)).isTrue()
        assertThat(ranMatching(/spotbugs:check/)).isTrue()
    }

    @Test
    void 'gradle lint runs checkstyle and spotbugs'() {
        step('gradleLint').call(cfgFor('gradle'))
        assertThat(ranMatching(/checkstyleMain/)).isTrue()
        assertThat(ranMatching(/spotbugsMain/)).isTrue()
    }

    @Test
    void 'node lint runs eslint and prettier'() {
        step('nodeLint').call(cfgFor('npm'))
        assertThat(ranMatching(/eslint \./)).isTrue()
        assertThat(ranMatching(/prettier --check/)).isTrue()
    }

    @Test
    void 'node lint typechecks when tsconfig is present'() {
        existingFiles['tsconfig.json'] = '{}'
        step('nodeLint').call(cfgFor('npm'))
        assertThat(ranMatching(/tsc --noEmit/)).isTrue()
    }

    @Test
    void 'node lint skips typecheck without tsconfig'() {
        step('nodeLint').call(cfgFor('npm'))
        assertThat(ranMatching(/tsc --noEmit/)).isFalse()
    }

    @Test
    void 'lint is skipped entirely when disabled'() {
        def cfg = cfgFor('go')
        cfg.lint.enabled = false
        step('lintApp').call(cfg)
        assertThat(ranMatching(/golangci-lint/)).isFalse()
    }

    @Test
    void 'an unknown build tool fails loudly rather than silently skipping'() {
        assertThatThrownBy { step('lintApp').call(cfgFor('cobol')) }
            .hasMessageContaining("No lint step for buildTool 'cobol'")
    }
}
