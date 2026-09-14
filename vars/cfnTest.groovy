// Policy checks over the templates.
//
// CloudFormation has no plan-time test framework, so this is static analysis:
// checkov for the common misconfigurations. Its JUnit report is what
// appTestReport publishes for cloudformation repos.
//
// Usage:
//   cfnTest(cfg)
// Params: cfg (Map) - pipeline config; reads cfg.infra.workingDir and cfg.lint.failOnError
// Returns: nothing; archives checkov-report.xml and errors if checkov fails and failOnError is true
def call(Map cfg) {
    logBanner 'Test: CloudFormation'

    def status = sh(
        script: "checkov -d ${cfg.infra.workingDir} --framework cloudformation --output cli --output junitxml --output-file-path console,checkov-report.xml --quiet",
        returnStatus: true
    )

    archiveArtifacts artifacts: 'checkov-report.xml', allowEmptyArchive: true

    if (status != 0 && cfg.lint.failOnError) {
        error 'checkov found policy violations in the CloudFormation templates'
    }
}
