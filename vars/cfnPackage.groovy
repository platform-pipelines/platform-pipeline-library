// `aws cloudformation package` uploads local artifacts (Lambda zips, nested
// stack bodies) to S3 and rewrites the template to point at them.
//
// Skipped when no artifactBucket is configured, since templates with no local
// references do not need it.
def call(Map cfg) {
    if (!cfg.infra.artifactBucket) {
        logInfo 'No infra.artifactBucket — skipping cloudformation package'
        return
    }

    logBanner 'Package: CloudFormation'

    withAwsCredentials(cfg) {
        sh """
            aws cloudformation package \\
              --template-file ${cfg.infra.template} \\
              --s3-bucket ${cfg.infra.artifactBucket} \\
              --s3-prefix ${cfg.appName}/${env.APP_VERSION} \\
              --output-template-file packaged-template.yaml
        """
    }

    archiveArtifacts artifacts: 'packaged-template.yaml', allowEmptyArchive: false
}
