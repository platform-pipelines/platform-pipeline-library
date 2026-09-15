import org.junit.Before
import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThatThrownBy

/**
 * Build output is published as assets on a GitHub release. A rebuild must
 * replace assets rather than fail on the duplicate name, and branch builds
 * must not flood the repo with releases unless publish.branchPattern says so.
 */
class PublishArtifactTest extends BaseTest {

    Map cfg = [appName: 'orders-api', buildTool: 'python', publish: [githubRelease: true, branchPattern: 'main']]

    List<Map> assets = []
    String releaseStatus = '200'

    @Before
    void stubRelease() {
        helper.registerAllowedMethod('findFiles', [Map]) {
            [[name: 'orders_api-1.4.0.tar.gz', path: 'dist/orders_api-1.4.0.tar.gz']]
        }
        helper.registerAllowedMethod('readJSON', [Map]) { Map m -> new groovy.json.JsonSlurper().parseText(m.text) }
        helper.registerAllowedMethod('readFile', [String]) { String p ->
            groovy.json.JsonOutput.toJson([
                id        : 7,
                html_url  : 'https://github.com/acme/payments-api/releases/tag/v1.4.0',
                upload_url: 'https://uploads.github.com/repos/acme/payments-api/releases/7/assets{?name,label}',
                assets    : assets,
            ])
        }
    }

    @Override
    String stubStdout(String script) {
        if (script.contains('/releases')) { return releaseStatus + '\n' }
        return super.stubStdout(script)
    }

    private String upload() { shellCommands.find { it.contains('--data-binary') }.toString() }

    @Test
    void 'does nothing when publish githubRelease is off'() {
        step('publishArtifact').call(cfg + [publish: [githubRelease: false, branchPattern: 'main']])
        assertThat(ranMatching(/curl/)).isFalse()
    }

    @Test
    void 'skips a branch that does not match publish branchPattern'() {
        binding.getVariable('env').BRANCH_NAME = 'feature/login'
        step('publishArtifact').call(cfg)
        assertThat(ranMatching(/curl/)).isFalse()
    }

    @Test
    void 'uploads each file to the release upload url'() {
        step('publishArtifact').call(cfg)

        assertThat(ranMatching(/releases\/tags\/v1\.4\.0/)).isTrue()
        assertThat(upload())
            .contains("@'dist/orders_api-1.4.0.tar.gz'")
            .contains('https://uploads.github.com/repos/acme/payments-api/releases/7/assets?name=orders_api-1.4.0.tar.gz')
        assertThat(ranMatching(/-X DELETE/)).isFalse()
    }

    @Test
    void 'a rebuild replaces an asset with the same name'() {
        assets = [[id: 99, name: 'orders_api-1.4.0.tar.gz']]
        step('publishArtifact').call(cfg)

        assertThat(ranMatching(/-X DELETE .*releases\/assets\/99/)).isTrue()
        assertThat(upload()).contains('--data-binary')
    }

    @Test
    void 'a pre-release version creates a pre-release'() {
        binding.getVariable('env').BRANCH_NAME = 'release/1.4'
        binding.getVariable('env').APP_VERSION = '1.4.0-rc.42'
        step('publishArtifact').call(cfg + [publish: [githubRelease: true, branchPattern: 'release/*']])

        assertThat(ranMatching(/releases\/tags\/v1\.4\.0-rc\.42/)).isTrue()
        assertThat(ranMatching(/"prerelease":true/)).isTrue()
    }

    @Test
    void 'fails when the release can be neither read nor created'() {
        releaseStatus = '403'
        assertThatThrownBy { step('publishArtifact').call(cfg) }
            .hasMessageContaining('Could not read or create release v1.4.0 in acme/payments-api (HTTP 403)')
        assertThat(ranMatching(/--data-binary/)).isFalse()
    }

    @Test
    void 'githubRelease rejects a tag that could break out of the url'() {
        assertThatThrownBy { step('githubRelease').call(repo: 'acme/api', tag: 'v1"; touch /tmp/pwned; "') }
            .hasMessageContaining('invalid tag')
        assertThat(ranMatching(/touch \/tmp\/pwned/)).isFalse()
    }
}
