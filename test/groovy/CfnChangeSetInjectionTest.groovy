import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThatThrownBy

/**
 * stack/changeSet/template/parameters/capabilities used to be interpolated
 * straight into a multi-line aws cli shell string. These lock in that a
 * malicious config value can no longer break out of it.
 */
class CfnChangeSetInjectionTest extends BaseTest {

    private Map cfg(Map overrides = [:]) {
        [appName: 'billing', infra: [template: 'template.yaml', capabilities: []]] + overrides
    }

    @Override
    String stubStdout(String script) {
        if (script.contains('cfn_changeset_summary')) { return 'no changes\n' }
        return super.stubStdout(script)
    }

    @Test
    void 'rejects a parameter key with shell metacharacters'() {
        def envCfg = [name: 'prod', parameters: ['ok; touch /tmp/pwned': 'x']]

        assertThatThrownBy {
            step('cfnChangeSet').call(cfg(), envCfg)
        }.hasMessageContaining('invalid CloudFormation parameter key')

        assertThat(ranMatching(/touch \/tmp\/pwned/)).isFalse()
    }

    @Test
    void 'rejects a capability with shell metacharacters'() {
        def envCfg = [name: 'prod']

        assertThatThrownBy {
            step('cfnChangeSet').call(cfg(infra: [template: 't.yaml', capabilities: ['CAPABILITY_IAM; touch /tmp/pwned']]), envCfg)
        }.hasMessageContaining('invalid CloudFormation capability')

        assertThat(ranMatching(/touch \/tmp\/pwned/)).isFalse()
    }

    @Test
    void 'stack name with shell metacharacters cannot break out of its quoted argument'() {
        def stack = 'billing"; touch /tmp/pwned; echo "'
        def envCfg = [name: 'prod', stackName: stack]

        step('cfnChangeSet').call(cfg(), envCfg)

        def quotedStack = step('shellQuote').call(stack)
        def createCmd = shellCommands.find { it.contains('create-change-set') }

        // The malicious text is present, but only as a single quoted
        // argument — never as a bare, shell-interpretable "; touch ..." run.
        assertThat(createCmd).contains("--stack-name ${quotedStack}".toString())
    }

    @Test
    void 'parameters are written to a json file rather than a shell-built flag'() {
        def envCfg = [name: 'prod', parameters: [Env: 'prod']]

        step('cfnChangeSet').call(cfg(), envCfg)

        assertThat(existingFiles['cfn-params.json']).contains('ParameterKey').contains('"Env"')
        assertThat(shellCommands.find { it.contains('create-change-set') }).contains('--parameters file://cfn-params.json')
    }
}
