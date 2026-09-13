import com.lesfurets.jenkins.unit.BasePipelineTest
import org.junit.Before

/**
 * Shared harness.
 *
 * The library's steps call each other by name — configLoad calls configMerge,
 * which calls configDefaults. JenkinsPipelineUnit loads one script at a time,
 * so registerLibrarySteps() makes every vars file callable as a step. That is
 * what lets cross-step calls resolve in tests.
 */
abstract class BaseTest extends BasePipelineTest {

    Map<String, String> existingFiles = [:]
    List<String> shellCommands = []

    @Before
    void setUp() {
        scriptRoots = ['vars']
        scriptExtension = 'groovy'
        super.setUp()

        binding.setVariable('env', [
            BUILD_NUMBER: '42',
            BRANCH_NAME : 'main',
            GIT_COMMIT  : 'ab12cd34ef56789012345678901234567890abcd',
            JOB_NAME    : 'payments-api/main',
            BUILD_URL   : 'http://jenkins.local/job/payments-api/42/',
            GIT_URL     : 'https://github.com/acme/payments-api.git',
            APP_VERSION : '1.4.0',
        ])

        binding.setVariable('currentBuild', [
            currentResult : 'SUCCESS',
            durationString: '2 min 10 sec',
            previousBuild : null,
            getBuildCauses: { -> [[_class: 'hudson.model.Cause$UserIdCause', userId: 'jane.doe']] },
        ])

        registerJenkinsSteps()
        registerLibrarySteps()
    }

    /** Loads a vars script by name, e.g. step('configLoad'). */
    def step(String name) {
        loadScript("${name}.groovy")
    }

    void configFile(String yaml) {
        existingFiles['.ci/config.yaml'] = yaml
    }

    boolean ranMatching(pattern) {
        shellCommands.any { it =~ pattern }
    }

    /**
     * Registers every vars file as a callable step so scripts can call each
     * other. Loading is lazy and cached — loading all 99 up front would be
     * slow, and most tests touch only a handful.
     */
    private void registerLibrarySteps() {
        Map cache = [:]

        new File('vars').listFiles()
            .findAll { it.name.endsWith('.groovy') }
            .each { file ->
                String name = file.name - '.groovy'

                Closure invoke = { Object... args ->
                    if (!cache.containsKey(name)) { cache[name] = loadScript(file.name) }
                    return args ? cache[name].call(*args) : cache[name].call()
                }

                // The arities the library actually uses.
                helper.registerAllowedMethod(name, [], invoke)
                helper.registerAllowedMethod(name, [String], invoke)
                helper.registerAllowedMethod(name, [Map], invoke)
                helper.registerAllowedMethod(name, [Map, Map], invoke)
                helper.registerAllowedMethod(name, [Map, String], invoke)
                helper.registerAllowedMethod(name, [String, String], invoke)
                helper.registerAllowedMethod(name, [String, String, String], invoke)
            }
    }

    private void registerJenkinsSteps() {
        helper.registerAllowedMethod('echo', [String]) { println "    $it" }
        helper.registerAllowedMethod('error', [String]) { throw new RuntimeException(it) }

        helper.registerAllowedMethod('fileExists', [String]) { String p -> existingFiles.containsKey(p) }
        helper.registerAllowedMethod('readFile', [String]) { String p -> existingFiles[p] ?: '' }
        helper.registerAllowedMethod('writeFile', [Map]) { Map m -> existingFiles[m.file] = m.text }
        helper.registerAllowedMethod('libraryResource', [String]) { String p -> "# stub: ${p}" }

        helper.registerAllowedMethod('readYaml', [Map]) { Map m ->
            new org.yaml.snakeyaml.Yaml().load(existingFiles[m.file] ?: '') ?: [:]
        }
        helper.registerAllowedMethod('readProperties', [Map]) { Map m ->
            Properties p = new Properties()
            p.load(new StringReader(existingFiles[m.file] ?: ''))
            return p as Map
        }

        helper.registerAllowedMethod('sh', [String]) { String cmd ->
            shellCommands << cmd
            return null
        }
        helper.registerAllowedMethod('sh', [Map]) { Map m ->
            shellCommands << m.script
            if (m.returnStdout) { return stubStdout(m.script) }
            if (m.returnStatus) { return stubStatus(m.script) }
            return null
        }

        helper.registerAllowedMethod('withCredentials', [List, Closure]) { l, c -> c.call() }
        helper.registerAllowedMethod('string', [Map]) { it }
        helper.registerAllowedMethod('usernamePassword', [Map]) { it }
        helper.registerAllowedMethod('text', [Map]) { it }

        helper.registerAllowedMethod('archiveArtifacts', [Map]) { }
        helper.registerAllowedMethod('junit', [Map]) { }
        helper.registerAllowedMethod('stash', [Map]) { }
        helper.registerAllowedMethod('unstash', [String]) { }
        helper.registerAllowedMethod('findFiles', [Map]) { [] }
        helper.registerAllowedMethod('checkout', [Object]) { }

        helper.registerAllowedMethod('timeout', [Map, Closure]) { m, c -> c.call() }
        helper.registerAllowedMethod('waitUntil', [Map, Closure]) { m, c -> c.call() }
        helper.registerAllowedMethod('node', [String, Closure]) { s, c -> c.call() }
        helper.registerAllowedMethod('stage', [String, Closure]) { s, c -> c.call() }
        helper.registerAllowedMethod('container', [String, Closure]) { s, c -> c.call() }
        helper.registerAllowedMethod('input', [Map]) { [APPROVER: 'ops.lead', REASON: 'CHG-1234'] }
        helper.registerAllowedMethod('parallel', [Map]) { Map m ->
            m.findAll { k, v -> v instanceof Closure }.each { k, v -> v.call() }
        }

        // docker.image(name).inside(args) { body }
        binding.setVariable('docker', [
            image: { String name ->
                [inside: { Object... a ->
                    Closure body = a.find { it instanceof Closure }
                    return body ? body.call() : null
                }]
            }
        ])
    }

    /** Override per-test to control what a given command returns. */
    String stubStdout(String script) {
        if (script.contains('git describe'))      { return 'v1.4.0\n' }
        if (script.contains('rev-parse HEAD'))    { return 'ab12cd34ef56789012345678901234567890abcd\n' }
        if (script.contains('remote.origin.url')) { return 'https://github.com/acme/payments-api.git\n' }
        if (script.contains('gofmt -l'))          { return '' }
        if (script.contains('coverage_percent'))  { return '85.0\n' }
        if (script.contains('trivy_summary'))     { return 'clean\n' }
        return ''
    }

    int stubStatus(String script) { return 0 }
}
