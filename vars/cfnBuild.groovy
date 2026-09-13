// Ask CloudFormation itself to validate each template. cfn-lint catches more,
// but only the service knows what the service will accept.
def call(Map cfg) {
    logBanner 'Build: CloudFormation'

    withAwsCredentials(cfg) {
        cfnTemplates(cfg).each { template ->
            sh "aws cloudformation validate-template --template-body file://${template} > /dev/null"
            logInfo "Validated ${template}"
        }
    }
}
