import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat

/**
 * Every step is public API. A new vars/ file without a docs page (or a page
 * that never made it into the mkdocs nav) is invisible to consumers, so the
 * build fails instead of the docs quietly falling behind.
 */
class DocsCoverageTest {

    // Only the nav block: the redirects plugin also lists every reference page.
    private final String nav = new File('mkdocs.yml').text.split(/(?m)^nav:$/)[1]

    // Step pages live in docs/reference/<section>/; guides elsewhere in docs/ are not step pages.
    private List<File> sections() {
        new File('docs/reference').listFiles().findAll { it.isDirectory() }
    }

    private List<String> steps() {
        new File('vars').listFiles()
            .findAll { it.name.endsWith('.groovy') }
            .collect { it.name - '.groovy' }
            .sort()
    }

    @Test
    void 'every step has a docs page'() {
        def missing = steps().findAll { name ->
            !sections().any { new File(it, "${name}.md").exists() }
        }
        assertThat(missing).as('steps without docs/reference/<section>/<name>.md').isEmpty()
    }

    @Test
    void 'every docs page is in the mkdocs nav'() {
        def pages = sections().collectMany { section ->
            section.listFiles().findAll { it.name.endsWith('.md') }.collect { "reference/${section.name}/${it.name}".toString() }
        }
        def missing = pages.findAll { !nav.contains(it) }
        assertThat(missing).as('docs pages missing from mkdocs.yml nav').isEmpty()
    }

    @Test
    void 'every docs page has a matching step or is an index'() {
        def known = steps() as Set
        def orphans = sections()
            .collectMany { it.listFiles().findAll { f -> f.name.endsWith('.md') }.collect { f -> f.name - '.md' } }
            .findAll { it != 'index' && !(it in known) }
        assertThat(orphans).as('docs pages for steps that no longer exist').isEmpty()
    }
}
