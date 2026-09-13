import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat

class ConfigEnvironmentsForTest extends BaseTest {

    private Map cfgWithEnvs() {
        [environments: [
            [name: 'dev',     branchPattern: '*'],
            [name: 'staging', branchPattern: 'main'],
            [name: 'prod',    branchPattern: 'main'],
        ]]
    }

    @Test
    void 'a feature branch reaches only dev'() {
        def names = step('configEnvironmentsFor').call(cfgWithEnvs(), 'feature/login')*.name
        assertThat(names).containsExactly('dev')
    }

    @Test
    void 'main reaches every environment'() {
        def names = step('configEnvironmentsFor').call(cfgWithEnvs(), 'main')*.name
        assertThat(names).containsExactly('dev', 'staging', 'prod')
    }

    @Test
    void 'a null branch reaches nothing rather than throwing'() {
        assertThat(step('configEnvironmentsFor').call(cfgWithEnvs(), null)).isEmpty()
    }

    @Test
    void 'a glob does not leak across path separators unexpectedly'() {
        def cfg = [environments: [[name: 'rel', branchPattern: 'release/*']]]
        assertThat(step('configEnvironmentsFor').call(cfg, 'release/1.4')*.name).containsExactly('rel')
        assertThat(step('configEnvironmentsFor').call(cfg, 'main')).isEmpty()
    }
}
