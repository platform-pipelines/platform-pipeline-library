import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat

/**
 * The riskiest string handling in the library. A pattern that silently fails
 * to match means the pipeline goes green while the old image stays deployed,
 * so every manifest shape we support gets a case here.
 */
class ManifestBumpImageTest extends BaseTest {

    private String bump(String yaml, String image) {
        step('manifestBumpImage').call(yaml, image)
    }

    @Test
    void 'bumps a kustomization newTag'() {
        def out = bump('images:\n  - name: ghcr.io/acme/api\n    newTag: 1.3.9\n',
                       'ghcr.io/acme/api:1.4.0')
        assertThat(out).contains('newTag: 1.4.0').doesNotContain('1.3.9')
    }

    @Test
    void 'bumps a plain deployment image line'() {
        def out = bump('spec:\n  containers:\n    - name: api\n      image: ghcr.io/acme/api:1.3.9\n',
                       'ghcr.io/acme/api:1.4.0')
        assertThat(out).contains('image: ghcr.io/acme/api:1.4.0')
    }

    @Test
    void 'bumps the inline list-item image form'() {
        def out = bump('containers:\n  - image: ghcr.io/acme/api:1.3.9\n',
                       'ghcr.io/acme/api:1.4.0')
        assertThat(out).contains('image: ghcr.io/acme/api:1.4.0')
    }

    @Test
    void 'leaves a sidecar image alone'() {
        def out = bump('''spec:
  containers:
    - name: api
      image: ghcr.io/acme/api:1.3.9
    - name: sidecar
      image: ghcr.io/acme/proxy:2.0.0
''', 'ghcr.io/acme/api:1.4.0')

        assertThat(out).contains('ghcr.io/acme/proxy:2.0.0')
        assertThat(out).contains('ghcr.io/acme/api:1.4.0')
    }

    @Test
    void 'handles a registry with a port'() {
        def out = bump('spec:\n  containers:\n    - image: registry.local:5000/acme/api:1.3.9\n',
                       'registry.local:5000/acme/api:1.4.0')
        assertThat(out).contains('registry.local:5000/acme/api:1.4.0')
    }

    @Test
    void 'bumps helm values style tags'() {
        def out = bump('image:\n  repository: ghcr.io/acme/api\n  tag: "1.3.9"\n',
                       'ghcr.io/acme/api:1.4.0')
        assertThat(out).contains('tag: "1.4.0"')
    }

    @Test
    void 'preserves comments'() {
        def out = bump('# owned by payments — do not hand-edit\nimages:\n  - name: ghcr.io/acme/api\n    newTag: 1.3.9\n',
                       'ghcr.io/acme/api:1.4.0')
        assertThat(out).contains('# owned by payments')
    }

    @Test
    void 'is a no-op when already at the target version'() {
        def input = 'images:\n  - name: ghcr.io/acme/api\n    newTag: 1.4.0\n'
        assertThat(bump(input, 'ghcr.io/acme/api:1.4.0')).isEqualTo(input)
    }

    @Test
    void 'handles a prerelease tag'() {
        def out = bump('images:\n  - name: ghcr.io/acme/api\n    newTag: 1.3.9\n',
                       'ghcr.io/acme/api:1.4.0-rc.42')
        assertThat(out).contains('newTag: 1.4.0-rc.42')
    }
}
