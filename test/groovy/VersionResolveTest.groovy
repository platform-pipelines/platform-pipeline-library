import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat

class VersionResolveTest extends BaseTest {

    @Test
    void 'main gets a clean semver'() {
        assertThat(step('versionResolve').call('main')).isEqualTo('1.4.0')
    }

    @Test
    void 'release branches get an rc suffix'() {
        assertThat(step('versionResolve').call('release/1.4').toString()).isEqualTo('1.4.0-rc.42')
    }

    @Test
    void 'hotfix branches carry the sha'() {
        assertThat(step('versionResolve').call('hotfix/urgent').toString()).isEqualTo('1.4.0-hotfix.42.gab12cd3')
    }

    @Test
    void 'feature branches are slugified'() {
        assertThat(step('versionResolve').call('feature/Add_Login-Page').toString())
            .isEqualTo('1.4.0-feature-add-login-page.42.gab12cd3')
    }
}
