import org.junit.Before
import org.junit.Test
import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThatThrownBy

/**
 * Bugs that passed every config-level test and only failed on a real agent:
 * containers that exit at once, tools with no writable HOME, AWS calls made
 * without credentials, installs into a read-only interpreter.
 */
class RuntimeRegressionTest extends BaseTest {

    int conftestStatus = 0
    List<String> insideArgs = []

    Map tfCfg = [
        appName: 'platform-network',
        infra  : [workingDir: 'terraform', varFiles: ['common.tfvars'], policyDir: 'policies',
                  region: 'eu-west-1', awsCredentialsId: 'aws-credentials'],
    ]

    @Override
    @Before
    void setUp() {
        super.setUp()
        binding.setVariable('docker', [
            image: { String name ->
                [inside: { Object... a ->
                    insideArgs << "${name} ${a.find { it instanceof String } ?: ''}".toString()
                    Closure body = a.find { it instanceof Closure }
                    return body ? body.call() : null
                }]
            }
        ])
    }

    @Override
    int stubStatus(String script) {
        if (script.contains('conftest')) { return conftestStatus }
        if (script.contains('terraform plan')) { return 2 }
        return 0
    }

    @Test
    void 'build containers clear the entrypoint and get a writable HOME'() {
        step('inContainer').call('hashicorp/terraform:1.16', '.terraform') { }

        assertThat(insideArgs).containsExactly("hashicorp/terraform:1.16 --entrypoint=''")
        assertThat(envVars*.toString()).anyMatch { it ==~ /HOME=.*@tmp\/home/ }
        assertThat(envVars*.toString()).anyMatch { it.startsWith('XDG_CACHE_HOME=') }
    }

    @Test
    void 'CI_TOOLBOX_IMAGE replaces the per-language and scanner images'() {
        binding.getVariable('env').CI_TOOLBOX_IMAGE = 'ghcr.io/acme/ci-toolbox:1.0.0'

        assertThat(step('appToolImage').call([buildTool: 'go'])).isEqualTo('ghcr.io/acme/ci-toolbox:1.0.0')
        step('inToolContainer').call('aquasec/trivy:latest') { }
        assertThat(insideArgs[0]).startsWith('ghcr.io/acme/ci-toolbox:1.0.0')
    }

    @Test
    void 'terraform plan runs with the environment credentials and assumed role'() {
        helper.registerAllowedMethod('withCredentials', [List, Closure]) { List l, Closure c ->
            envVars << "credentials=${l[0].credentialsId}".toString()
            c.call()
        }

        step('terraformPlan').call(tfCfg, [name: 'prod', region: 'us-east-2', awsCredentialsId: 'aws-prod'])

        assertThat(envVars*.toString()).contains('AWS_REGION=us-east-2', 'credentials=aws-prod')
        assertThat(ranMatching(/terraform init .*-reconfigure/)).isTrue()
        assertThat(ranMatching(/terraform plan .*-var-file='common\.tfvars' -out=tfplan-prod/)).isTrue()
    }

    @Test
    void 'policies are checked against the environment plan'() {
        existingFiles['policies'] = ''

        step('terraformPlan').call(tfCfg, [name: 'prod'])

        assertThat(ranMatching(/conftest test --no-color --policy 'policies' tfplan-prod\.json/)).isTrue()
    }

    @Test
    void 'a policy failure stops the plan before it can be approved'() {
        existingFiles['policies'] = ''
        conftestStatus = 1

        assertThatThrownBy { step('terraformPlan').call(tfCfg, [name: 'prod']) }
            .hasMessageContaining('Policy check failed for the prod plan')
    }

    @Test
    void 'terraform build and test need no backend and no credentials'() {
        existingFiles['tests'] = ''
        helper.registerAllowedMethod('withCredentials', [List, Closure]) { l, c ->
            throw new IllegalStateException('credentials requested')
        }

        step('terraformBuild').call(tfCfg)
        step('terraformTest').call(tfCfg)

        assertThat(shellCommands.findAll { it.contains('terraform init') })
            .allMatch { it.contains('-backend=false') }
        assertThat(ranMatching(/terraform test/)).isTrue()
        assertThat(ranMatching(/terraform plan/)).isFalse()
    }

    @Test
    void 'terraform apply runs with credentials'() {
        existingFiles['tfplan-prod'] = ''
        helper.registerAllowedMethod('withCredentials', [List, Closure]) { List l, Closure c ->
            envVars << 'credentials'
            c.call()
        }

        step('terraformApply').call(tfCfg, [name: 'prod'], 'tfplan-prod')

        assertThat(envVars*.toString()).contains('credentials', 'AWS_REGION=eu-west-1')
        assertThat(ranMatching(/terraform apply .* tfplan-prod/)).isTrue()
    }

    @Test
    void 'python steps install into a venv, never the system interpreter'() {
        existingFiles['requirements.txt'] = 'flask'
        existingFiles['requirements-dev.txt'] = 'pytest'

        step('pythonBuild').call([:])
        step('pythonTest').call([:])

        assertThat(ranMatching(/^python3 -m venv \.venv$/)).isTrue()
        assertThat(ranMatching(/\.venv\/bin\/python -m pip install .* -r requirements-dev\.txt/)).isTrue()
        assertThat(ranMatching(/\.venv\/bin\/python -m pytest/)).isTrue()
        assertThat(shellCommands).noneMatch { it.startsWith('pip install') }
    }

    @Test
    void 'go package builds the cmd directory when there is one'() {
        step('goPackage').call([appName: 'edge-router'])
        assertThat(ranMatching(/-o dist\/edge-router \.$/)).isTrue()

        existingFiles['cmd/edge-router'] = ''
        step('goPackage').call([appName: 'edge-router'])
        assertThat(ranMatching(/-o dist\/edge-router \.\/cmd\/edge-router$/)).isTrue()
    }

    @Test
    void 'node test installs dependencies on a fresh workspace'() {
        step('nodeTest').call([:])
        assertThat(ranMatching(/npm ci/)).isTrue()
    }
}
