import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThatThrownBy

/**
 * repo/branch/path/method all used to be interpolated straight into a shell
 * string. These lock in that a malicious value can no longer break out of
 * the quoted argument it now lands in.
 */
class GithubStepsInjectionTest extends BaseTest {

    @Test
    void 'githubFetchFile rejects a repo slug that is not owner-name shaped'() {
        assertThatThrownBy {
            step('githubFetchFile').call(repo: 'acme/api"; touch /tmp/pwned; echo "', path: 'README.md')
        }.hasMessageContaining('invalid repo slug')

        assertThat(ranMatching(/touch \/tmp\/pwned/)).isFalse()
    }

    @Test
    void 'githubFetchFile puts the whole url in a single quoted shell token'() {
        def path = 'a"; touch /tmp/pwned; echo "'
        step('githubFetchFile').call(repo: 'acme/api', path: path)

        def url = "${step('githubApiUrl').call()}/repos/acme/api/contents/${path}?ref=main"
        def quotedUrl = step('shellQuote').call(url)
        def curlCmd = shellCommands.find { it.contains('curl') }.toString()

        // The malicious text is present, but only as a single quoted argument
        // — never as a bare, shell-interpretable "; touch ..." sequence.
        assertThat(curlCmd).contains(quotedUrl)
    }

    @Test
    void 'githubApiRequest rejects an unsupported http method'() {
        assertThatThrownBy {
            step('githubApiRequest').call(method: 'GET; rm -rf /', path: '/repos/acme/api')
        }.hasMessageContaining('unsupported method')

        assertThat(ranMatching(/rm -rf/)).isFalse()
    }

    @Test
    void 'githubApiRequest puts the whole url in a single quoted shell token'() {
        def path = '/repos/acme/api"; touch /tmp/pwned; echo "'
        step('githubApiRequest').call(method: 'GET', path: path)

        def url = "${step('githubApiUrl').call()}${path}"
        def quotedUrl = step('shellQuote').call(url)
        def curlCmd = shellCommands.find { it.contains('curl') }.toString()

        assertThat(curlCmd).contains(quotedUrl)
    }
}
