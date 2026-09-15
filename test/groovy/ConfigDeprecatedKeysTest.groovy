import org.junit.Before
import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat

/**
 * Keys that moved out of `extra` must keep working for repos that have not
 * migrated yet, and must tell those repos where the key lives now.
 */
class ConfigDeprecatedKeysTest extends BaseTest {

    List<String> echoed = []

    @Before
    void captureEcho() {
        helper.registerAllowedMethod('echo', [String]) { String m -> echoed << m }
    }

    private static final String BASE = 'appName: api\nbuildTool: maven\ncontainerize: false\n'

    @Test
    void 'an old extra key still applies and logs where it moved'() {
        configFile(BASE + 'extra:\n  sonarSources: src\n  allowSelfApproval: true\n')
        def cfg = step('configLoad').call()

        assertThat(cfg.quality.sonarSources).isEqualTo('src')
        assertThat(cfg.approval.allowSelfApproval as boolean).isTrue()
        assertThat(echoed).anyMatch { it.contains('extra.sonarSources is deprecated — move it to quality.sonarSources') }
    }

    @Test
    void 'the new key wins when both are written'() {
        configFile(BASE + 'extra:\n  dependencyCheckCvss: 4\nquality:\n  dependencyCheckCvss: 9\n')
        def cfg = step('configLoad').call()

        assertThat(cfg.quality.dependencyCheckCvss).isEqualTo(9)
        assertThat(echoed).anyMatch { it.contains('extra.dependencyCheckCvss is ignored') }
    }

    @Test
    void 'new keys have sensible defaults when nothing is written'() {
        configFile(BASE)
        def cfg = step('configLoad').call()

        assertThat(cfg.publish.githubRelease as boolean).isFalse()
        assertThat(cfg.publish.branchPattern).isEqualTo('main')
        assertThat(cfg.approval.allowSelfApproval as boolean).isFalse()
        assertThat(cfg.quality.dependencyCheckCvss).isEqualTo(7)
        assertThat(cfg.quality.sonarSources).isEqualTo('.')
        assertThat(echoed).noneMatch { it.contains('deprecated') }
    }

    @Test
    void 'every deprecated key points at a key that exists in configDefaults'() {
        Map defaults = step('configDefaults').call()
        step('configDeprecatedKeys').call().each { oldPath, newPath ->
            def n = newPath.tokenize('.')
            assertThat((defaults[n[0]] as Map).containsKey(n[1])).as(newPath.toString()).isTrue()
        }
    }

    @Test
    void 'notify on survives YAML 1_1 reading the bare key as boolean true'() {
        configFile(BASE + 'notify:\n  on: failure\n')
        def cfg = step('configLoad').call()

        assertThat(cfg.notify.on).isEqualTo('failure')
        assertThat(echoed).noneMatch { it.contains('unknown config key') }
    }

    @Test
    void 'an unknown key is logged as a warning but does not fail the load'() {
        configFile(BASE + 'quality:\n  minCoverge: 80\n')
        step('configLoad').call()
        assertThat(echoed).anyMatch { it.contains("did you mean 'minCoverage'") }
    }
}
