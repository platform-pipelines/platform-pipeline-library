import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThatThrownBy

/**
 * cdPipeline promotes by reading what the source environment actually runs.
 * Reading the wrong tag (or none, silently) would ship an untested build to
 * prod, so the read side is covered for every manifest shape the write side
 * (manifestBumpImage) supports.
 */
class CdPromotionTest extends BaseTest {

    String stagingManifest = 'images:\n  - name: ghcr.io/acme/api\n    newTag: 1.4.0\n'

    Map cfg = [
        appName       : 'api',
        imageRepo     : 'ghcr.io/acme/api',
        deployStrategy: 'gitops',
        gitopsRepo    : 'acme/gitops',
        gitopsBranch  : 'main',
        environments  : [
            [name: 'staging', manifestPath: 'apps/api/staging/kustomization.yaml'],
            [name: 'prod', manifestPath: 'apps/api/prod/kustomization.yaml', promoteFrom: 'staging'],
        ],
    ]

    @Override
    String stubStdout(String script) {
        if (script.contains('vnd.github.raw')) { return stagingManifest }
        return super.stubStdout(script)
    }

    private String current(String yaml) {
        step('manifestCurrentImage').call(yaml, 'ghcr.io/acme/api')
    }

    @Test
    void 'reads the tag from each manifest shape'() {
        assertThat(current('images:\n  - name: ghcr.io/acme/api\n    newTag: 1.3.9\n')).isEqualTo('1.3.9')
        assertThat(current('spec:\n  containers:\n    - image: ghcr.io/acme/api:1.3.9\n')).isEqualTo('1.3.9')
        assertThat(current('image:\n  repository: ghcr.io/acme/api\n  tag: "1.3.9"\n')).isEqualTo('1.3.9')
    }

    @Test
    void 'ignores a sidecar image and a registry port'() {
        assertThat(current('containers:\n  - image: ghcr.io/acme/proxy:9.9.9\n  - image: ghcr.io/acme/api:1.3.9\n'))
            .isEqualTo('1.3.9')
        assertThat(step('manifestCurrentImage').call('- image: registry.local:5000/acme/api:2.0.0\n', 'registry.local:5000/acme/api'))
            .isEqualTo('2.0.0')
    }

    @Test
    void 'returns null when the manifest has no tag for the repo'() {
        assertThat(current('containers:\n  - image: ghcr.io/acme/other:1.0.0\n')).isNull()
        assertThat(current('')).isNull()
    }

    @Test
    void 'read and bump agree'() {
        def bumped = step('manifestBumpImage').call(stagingManifest, 'ghcr.io/acme/api:2.1.0')
        assertThat(current(bumped)).isEqualTo('2.1.0')
    }

    @Test
    void 'an explicit tag wins over promotion'() {
        assertThat(step('cdResolveImage').call(cfg, cfg.environments[1], '1.2.3')).isEqualTo('1.2.3')
        assertThat(ranMatching(/vnd\.github\.raw/)).isFalse()
    }

    @Test
    void 'an empty tag promotes what the source environment runs'() {
        assertThat(step('cdResolveImage').call(cfg, cfg.environments[1], '')).isEqualTo('1.4.0')
        assertThat(ranMatching(/apps\/api\/staging\/kustomization\.yaml/)).isTrue()
    }

    @Test
    void 'rejects a tag carrying shell metacharacters'() {
        assertThatThrownBy { step('cdResolveImage').call(cfg, cfg.environments[1], '1.0; rm -rf /') }
            .hasMessageContaining('is not a valid image tag')
    }

    @Test
    void 'requires a tag when there is nothing to promote from'() {
        assertThatThrownBy { step('cdResolveImage').call(cfg, cfg.environments[0], null) }
            .hasMessageContaining('IMAGE_TAG is required: staging has no promoteFrom')
    }

    @Test
    void 'fails when the source environment runs no tag for the repo'() {
        stagingManifest = 'containers:\n  - image: ghcr.io/acme/other:1.0.0\n'
        assertThatThrownBy { step('cdResolveImage').call(cfg, cfg.environments[1], '') }
            .hasMessageContaining('Could not find a ghcr.io/acme/api tag running in staging')
    }

    @Test
    void 'promoteFrom must name another declared environment'() {
        configFile('''
appName: api
buildTool: go
imageRepo: ghcr.io/acme/api
gitopsRepo: acme/gitops
environments:
  - name: staging
    manifestPath: a.yaml
    promoteFrom: staging
  - name: prod
    manifestPath: b.yaml
    promoteFrom: stagign
''')
        assertThatThrownBy { step('configLoad').call() }
            .hasMessageContaining('(staging) cannot promote from itself')
            .hasMessageContaining("(prod) promoteFrom 'stagign' is not a declared environment")
    }

    @Test
    void 'imageDigest fails when the tag is not in the registry'() {
        helper.registerAllowedMethod('sh', [Map]) { Map m ->
            shellCommands << m.script
            return m.returnStdout ? 'MANIFEST_UNKNOWN: manifest unknown\n' : 0
        }
        assertThatThrownBy { step('imageDigest').call(cfg, '9.9.9') }
            .hasMessageContaining('ghcr.io/acme/api:9.9.9 was not found in the registry')
    }

    @Test
    void 'imageDigest records the digest'() {
        def digest = 'sha256:' + ('a' * 64)
        helper.registerAllowedMethod('sh', [Map]) { Map m ->
            shellCommands << m.script
            return m.returnStdout ? digest + '\n' : 0
        }
        assertThat(step('imageDigest').call(cfg, '1.4.0')).isEqualTo(digest)
        assertThat(binding.getVariable('env').IMAGE_DIGEST).isEqualTo(digest)
    }
}
