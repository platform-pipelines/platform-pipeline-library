import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat

class VersionSlugTest extends BaseTest {

    @Test
    void 'lowercases and replaces separators'() {
        assertThat(step('versionSlug').call('Feature/ABC_123')).isEqualTo('feature-abc-123')
    }

    @Test
    void 'is bounded so image tags stay readable'() {
        def s = step('versionSlug').call('a-very-long-branch-name-that-somebody-typed-without-thinking')
        assertThat(s.length()).isLessThanOrEqualTo(30)
    }

    @Test
    void 'never ends in a dash after truncation'() {
        // truncation at 30 can land on a separator, which is illegal in a tag
        def s = step('versionSlug').call('abcdefghij-klmnopqrst-uvwxyz-abcdefg')
        assertThat(s).doesNotEndWith('-')
    }
}
