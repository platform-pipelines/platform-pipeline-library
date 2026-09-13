// Assumes a role and runs the body with the temporary credentials.
//
// The session name carries the build so CloudTrail shows which pipeline run
// made each API call — that is the difference between an audit you can answer
// and one you cannot.
//
// Usage:
//   assumeAwsRole('arn:aws:iam::123456789012:role/deploy') { sh 'aws s3 ls' }
// Params: roleArn (String) - IAM role ARN to assume; must match a strict ARN shape
//         body (Closure) - code to run with the assumed role's credentials exported
// Returns: nothing; errors if roleArn is malformed or sts assume-role does not return 3 credential fields
def call(String roleArn, Closure body) {
    if (!(roleArn ==~ /^arn:aws:iam::\d{12}:role\/[\w+=,.@-]+$/)) {
        error "assumeAwsRole: invalid role ARN '${roleArn}'"
    }
    def session = "jenkins-${env.BUILD_NUMBER}-${env.GIT_SHORT_SHA}".take(64)

    def creds = sh(
        script: """
            aws sts assume-role \\
              --role-arn ${shellQuote(roleArn)} \\
              --role-session-name ${shellQuote(session)} \\
              --duration-seconds 3600 \\
              --query 'Credentials.[AccessKeyId,SecretAccessKey,SessionToken]' \\
              --output text
        """,
        returnStdout: true
    ).trim().split(/\s+/)

    if (creds.size() != 3) {
        error "Failed to assume ${roleArn}"
    }

    withEnv([
        "AWS_ACCESS_KEY_ID=${creds[0]}",
        "AWS_SECRET_ACCESS_KEY=${creds[1]}",
        "AWS_SESSION_TOKEN=${creds[2]}",
    ]) {
        body()
    }
}
