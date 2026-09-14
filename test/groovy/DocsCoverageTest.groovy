import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat

/**
 * Every step is public API. A new vars/ file without a docs page (or a page
 * that never made it into the mkdocs nav) is invisible to consumers, so the
 * build fails instead of the docs quietly falling behind.
 */
class DocsCoverageTest {

    private final String nav = new File('mkdocs.yml').text

    private List<String> steps() {
        new File('vars').listFiles()
            .findAll { it.name.endsWith('.groovy') }
            .collect { it.name - '.groovy' }
            .sort()
    }

    @Test
    void 'every step has a docs page'() {
        def missing = steps().findAll { name ->
            !new File('docs').listFiles().any { it.isDirectory() && new File(it, "${name}.md").exists() }
        }
        assertThat(missing).as('steps without docs/<section>/<name>.md').isEmpty()
    }

    @Test
    void 'every docs page is in the mkdocs nav'() {
        def pages = new File('docs').listFiles()
            .findAll { it.isDirectory() }
            .collectMany { section ->
                section.listFiles().findAll { it.name.endsWith('.md') }.collect { "${section.name}/${it.name}".toString() }
            }
        def missing = pages.findAll { !nav.contains(it) }
        assertThat(missing).as('docs pages missing from mkdocs.yml nav').isEmpty()
    }

    @Test
    void 'every docs page has a matching step or is an index'() {
        def known = steps() as Set
        def orphans = new File('docs').listFiles()
            .findAll { it.isDirectory() }
            .collectMany { it.listFiles().findAll { f -> f.name.endsWith('.md') }.collect { f -> f.name - '.md' } }
            .findAll { it != 'index' && !(it in known) }
        assertThat(orphans).as('docs pages for steps that no longer exist').isEmpty()
    }
}
