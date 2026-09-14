import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThatThrownBy

/**
 * Regressions for bugs that only showed up at runtime on a real agent.
 */
class InfraStepsRegressionTest extends BaseTest {

    int hadolintStatus = 0

    @Override
    int stubStatus(String script) {
        return script.contains('hadolint') ? hadolintStatus : 0
    }

    @Test
    void 'terraformInit runs inside the working directory without shadowing the dir step'() {
        // A local variable named `dir` used to shadow the dir() step, so this
        // threw MissingMethodException on every terraform build.
        step('terraformInit').call([infra: [workingDir: 'terraform', backendConfig: 'b.hcl']], [workspace: 'dev'])

        assertThat(ranMatching(/terraform init .*-backend-config=b\.hcl/)).isTrue()
        assertThat(ranMatching(/workspace select -or-create dev/)).isTrue()
    }

    @Test
    void 'dockerOnlyLint fails the build when hadolint fails and failOnError is set'() {
        hadolintStatus = 1
        assertThatThrownBy {
            step('dockerOnlyLint').call([dockerfile: 'Dockerfile', lint: [failOnError: true]])
        }.hasMessageContaining('hadolint reported problems')
    }

    @Test
    void 'dockerOnlyLint is report-only when failOnError is false'() {
        hadolintStatus = 1
        step('dockerOnlyLint').call([dockerfile: 'Dockerfile', lint: [failOnError: false]])
        assertThat(ranMatching(/^hadolint Dockerfile$/)).isTrue()
    }

    @Test
    void 'infra repos do not publish lint reports as test results'() {
        assertThat(step('appTestReport').call([buildTool: 'terraform'])).isNull()
        assertThat(step('appTestReport').call([buildTool: 'cloudformation'])).isEqualTo('checkov-report.xml')
    }
}
