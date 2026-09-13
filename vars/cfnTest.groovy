// Policy checks over the templates.
//
// CloudFormation has no plan-time test framework, so this is static analysis:
// checkov for the common misconfigurations, cfn-guard for rules you write.
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
