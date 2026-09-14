import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat

/**
 * A typo in config silently falls back to the default, so the gate a team
 * believes is on never runs. These lock in that typos are reported, with a
 * useful hint, and that legitimately free-form sections stay quiet.
 */
class ConfigUnknownKeysTest extends BaseTest {

    private List<String> warnings(Map raw) {
        step('configUnknownKeys').call(raw)*.toString()
    }

    @Test
    void 'a fully valid config produces no warnings'() {
        def raw = [appName: 'api', buildTool: 'go', quality: [minCoverage: 80, sonar: true],
                   environments: [[name: 'dev', manifestPath: 'a.yaml', branchPattern: '*']]]
        assertThat(warnings(raw)).isEmpty()
    }

    @Test
    void 'a misspelled nested key gets a did-you-mean hint'() {
        List<String> w = warnings([quality: [minCoverge: 80]])
        assertThat(w).hasSize(1)
        assertThat(w.first()).contains("'quality.minCoverge'").contains("did you mean 'minCoverage'")
    }

    @Test
    void 'a misspelled top-level key gets a hint'() {
        assertThat(warnings([qualty: [sonar: true]]).first()).contains("did you mean 'quality'")
    }

    @Test
    void 'an unrelated key is reported without a misleading hint'() {
        List<String> w = warnings([kubernetesClusterName: 'prod'])
        assertThat(w.first()).contains('kubernetesClusterName').doesNotContain('did you mean')
    }

    @Test
    void 'environment entries are checked against the environment defaults'() {
        List<String> w = warnings([environments: [[name: 'prod', aprovers: ['ops']]]])
        assertThat(w.first()).contains("'environments[0].aprovers'").contains("did you mean 'approvers'")
    }

    @Test
    void 'extra is free-form and never checked'() {
        assertThat(warnings([extra: [owningTeam: 'payments', anything: [goes: true]]])).isEmpty()
    }

    @Test
    void 'deprecated keys are handled by the deprecation path, not reported as unknown'() {
        assertThat(warnings([extra: [nexusRepo: 'maven-releases']])).isEmpty()
    }

    @Test
    void 'every key in configDefaults is itself recognised'() {
        Map defaults = step('configDefaults').call()
        assertThat(warnings(defaults)).isEmpty()
    }
}
