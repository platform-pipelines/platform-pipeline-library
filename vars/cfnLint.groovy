// CloudFormation lint: cfn-lint over every template, then a syntax check by
// the service itself.
def call(Map cfg) {
    logBanner 'Lint: CloudFormation'

    def templates = cfnTemplates(cfg)
    if (!templates) {
        error "No CloudFormation templates found under ${cfg.infra.workingDir}"
    }

    def status = sh(
        script: "cfn-lint ${templates.join(' ')} --format junit > cfn-lint-report.xml 2>/dev/null; cfn-lint ${templates.join(' ')}",
        returnStatus: true
    )

    if (status != 0 && cfg.lint.failOnError) {
        error 'cfn-lint reported problems'
    }
}
