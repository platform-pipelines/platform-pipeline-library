// Assumes a role and runs the body with the temporary credentials.
//
// The session name carries the build so CloudTrail shows which pipeline run
// made each API call — that is the difference between an audit you can answer
// and one you cannot.
def call(String roleArn, Closure body) {
    def session = "jenkins-${env.BUILD_NUMBER}-${env.GIT_SHORT_SHA}".take(64)

    def creds = sh(
        script: """
            aws sts assume-role \\
              --role-arn ${roleArn} \\
              --role-session-name ${session} \\
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
