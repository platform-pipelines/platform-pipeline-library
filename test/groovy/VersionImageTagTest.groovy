import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat

class VersionImageTagTest extends BaseTest {

    @Test
    void 'contains only characters legal in an OCI tag'() {
        assertThat(step('versionImageTag').call('feature/JIRA-123_fix')).matches(/[A-Za-z0-9._-]+/)
    }

    @Test
    void 'leaves a plain semver untouched'() {
        assertThat(step('versionImageTag').call('main')).isEqualTo('1.4.0')
    }
}
