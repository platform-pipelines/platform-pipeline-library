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
    List<String> envVars = []

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
     * other. Loading is lazy and cached — loading every step up front would be
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
                helper.with {
                    registerAllowedMethod(name, [], invoke)
                    registerAllowedMethod(name, [String], invoke)
                    registerAllowedMethod(name, [Map], invoke)
                    registerAllowedMethod(name, [Map, Map], invoke)
                    registerAllowedMethod(name, [Map, String], invoke)
                    registerAllowedMethod(name, [String, Map], invoke)
                    registerAllowedMethod(name, [Map, Map, String], invoke)
                    registerAllowedMethod(name, [Map, String, String], invoke)
                    registerAllowedMethod(name, [String, String], invoke)
                    registerAllowedMethod(name, [String, List], invoke)
                    registerAllowedMethod(name, [String, String, String], invoke)
                    registerAllowedMethod(name, [String, Closure], invoke)
                    registerAllowedMethod(name, [String, String, Closure], invoke)
                    registerAllowedMethod(name, [Map, Closure], invoke)
                    registerAllowedMethod(name, [Map, Map, Closure], invoke)
                }
            }
    }

    private void registerJenkinsSteps() {
        helper.with {
            registerAllowedMethod('echo', [String]) { println "    $it" }
            registerAllowedMethod('error', [String]) { throw new RuntimeException(it) }

            registerAllowedMethod('fileExists', [String]) { String p -> existingFiles.containsKey(p) }
            registerAllowedMethod('readFile', [String]) { String p -> existingFiles[p] ?: '' }
            registerAllowedMethod('writeFile', [Map]) { Map m -> existingFiles[m.file] = m.text }
            registerAllowedMethod('writeJSON', [Map]) { Map m -> existingFiles[m.file] = groovy.json.JsonOutput.toJson(m.json) }
            registerAllowedMethod('readJSON', [Map]) { Map m ->
                new groovy.json.JsonSlurper().parseText(existingFiles[m.file] ?: '{}')
            }
            registerAllowedMethod('libraryResource', [String]) { String p -> "# stub: ${p}" }

            registerAllowedMethod('readYaml', [Map]) { Map m ->
                new org.yaml.snakeyaml.Yaml().load(existingFiles[m.file] ?: '') ?: [:]
            }
            registerAllowedMethod('readProperties', [Map]) { Map m ->
                Properties p = new Properties()
                p.load(new StringReader(existingFiles[m.file] ?: ''))
                return p as Map
            }

            registerAllowedMethod('sh', [String]) { String cmd ->
                shellCommands << cmd
                return null
            }
            registerAllowedMethod('sh', [Map]) { Map m ->
                shellCommands << m.script
                if (m.returnStdout) { return stubStdout(m.script) }
                if (m.returnStatus) { return stubStatus(m.script) }
                return null
            }

            registerAllowedMethod('withCredentials', [List, Closure]) { l, c -> c.call() }
            registerAllowedMethod('withEnv', [List, Closure]) { List vars, Closure c ->
                vars.each { envVars << it }
                c.call()
            }
            registerAllowedMethod('string', [Map]) { it }
            registerAllowedMethod('usernamePassword', [Map]) { it }
            registerAllowedMethod('text', [Map]) { it }

            registerAllowedMethod('archiveArtifacts', [Map]) { }
            registerAllowedMethod('junit', [Map]) { }
            registerAllowedMethod('unstable', [String]) { }
            registerAllowedMethod('stash', [Map]) { }
            registerAllowedMethod('unstash', [String]) { }
            registerAllowedMethod('findFiles', [Map]) { [] }
            registerAllowedMethod('checkout', [Object]) { }

            registerAllowedMethod('timeout', [Map, Closure]) { m, c -> c.call() }
            registerAllowedMethod('waitUntil', [Map, Closure]) { m, c -> c.call() }
            registerAllowedMethod('node', [String, Closure]) { s, c -> c.call() }
            registerAllowedMethod('dir', [String, Closure]) { s, c -> c.call() }
            registerAllowedMethod('stage', [String, Closure]) { s, c -> c.call() }
            registerAllowedMethod('container', [String, Closure]) { s, c -> c.call() }
            registerAllowedMethod('input', [Map]) { [APPROVER: 'ops.lead', REASON: 'CHG-1234'] }
            registerAllowedMethod('parallel', [Map]) { Map m ->
                m.findAll { k, v -> v instanceof Closure }.each { k, v -> v.call() }
            }
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
