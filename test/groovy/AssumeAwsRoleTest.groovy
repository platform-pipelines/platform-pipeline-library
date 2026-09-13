import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThatThrownBy

/**
 * roleArn reaches a raw sh() string, so a malicious/malformed value must be
 * rejected before it gets anywhere near the shell.
 */
class AssumeAwsRoleTest extends BaseTest {

    @Override
    String stubStdout(String script) {
        if (script.contains('assume-role')) {
            return 'AKIAEXAMPLE SECRETEXAMPLE SESSIONTOKENEXAMPLE\n'
        }
        return super.stubStdout(script)
    }

    @Test
    void 'assumes a valid role and exports temporary credentials'() {
        def ran = false
        step('assumeAwsRole').call('arn:aws:iam::123456789012:role/deploy') { ran = true }

        assertThat(ran).isTrue()
        def envVarStrings = envVars*.toString()
        assertThat(envVarStrings).contains('AWS_ACCESS_KEY_ID=AKIAEXAMPLE')
        assertThat(envVarStrings).contains('AWS_SECRET_ACCESS_KEY=SECRETEXAMPLE')
        assertThat(envVarStrings).contains('AWS_SESSION_TOKEN=SESSIONTOKENEXAMPLE')
        assertThat(ranMatching(/assume-role/)).isTrue()
    }

    @Test
    void 'rejects a role arn carrying shell metacharacters'() {
        assertThatThrownBy {
            step('assumeAwsRole').call('arn:aws:iam::123456789012:role/deploy"; rm -rf /; echo "') {}
        }.hasMessageContaining('invalid role ARN')

        assertThat(ranMatching(/rm -rf/)).isFalse()
    }

    @Test
    void 'fails clearly when sts returns malformed output'() {
        helper.registerAllowedMethod('sh', [Map]) { Map m ->
            shellCommands << m.script
            if (m.returnStdout) { return 'only-one-field\n' }
            return null
        }

        assertThatThrownBy {
            step('assumeAwsRole').call('arn:aws:iam::123456789012:role/deploy') {}
        }.hasMessageContaining('Failed to assume')
    }
}
