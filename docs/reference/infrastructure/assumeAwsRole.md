# assumeAwsRole

Assumes an IAM role via STS and runs a closure with the temporary credentials
exported as environment variables.

The session name carries the build number and commit so CloudTrail shows
exactly which pipeline run made each API call — that's the difference
between an audit you can answer and one you cannot.

!!! note "Input validation"
    `roleArn` is validated against a strict ARN shape and shell-quoted before
    it reaches `aws sts assume-role`, so a malformed or malicious value is
    rejected up front rather than reaching a shell command.

## Syntax

```groovy
assumeAwsRole(String roleArn) {
    // steps that use the role
}
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `roleArn` | `String` | yes | — | IAM role ARN; must match `arn:aws:iam::<12 digits>:role/<name>`. |
| `body` | `Closure` | yes | — | Steps to run with the assumed role's credentials. |

Base credentials that are allowed to call `sts:AssumeRole` must already be in
the environment — normally from [withAwsCredentials](withAwsCredentials.md).

## Returns

Nothing. Inside `body` these are set for a 1-hour session:

| Variable | Sample value |
|---|---|
| `AWS_ACCESS_KEY_ID` | `ASIA…` |
| `AWS_SECRET_ACCESS_KEY` | `…` |
| `AWS_SESSION_TOKEN` | `…` |

The STS session name is `jenkins-<BUILD_NUMBER>-<GIT_SHORT_SHA>` (max 64
characters), e.g. `jenkins-42-ab12cd3`.

Fails the build with `assumeAwsRole: invalid role ARN '…'` for a bad ARN, or
`Failed to assume <arn>` if STS doesn't return credentials.

## Examples

```groovy
assumeAwsRole('arn:aws:iam::123456789012:role/deploy') {
    sh 'aws sts get-caller-identity'
}
```

Output:

```json
{
  "UserId": "AROA...:jenkins-42-ab12cd3",
  "Account": "123456789012",
  "Arn": "arn:aws:sts::123456789012:assumed-role/deploy/jenkins-42-ab12cd3"
}
```

Using it with base credentials from Jenkins directly:

```groovy
withCredentials([usernamePassword(credentialsId: 'aws-credentials',
                                  usernameVariable: 'AWS_ACCESS_KEY_ID',
                                  passwordVariable: 'AWS_SECRET_ACCESS_KEY')]) {
    withEnv(['AWS_REGION=eu-west-1']) {
        assumeAwsRole('arn:aws:iam::111122223333:role/JenkinsDeploy') {
            sh 'aws s3 ls s3://acme-terraform-state/'
        }
    }
}
```

| `roleArn` | Accepted? |
|---|---|
| `arn:aws:iam::111122223333:role/JenkinsDeploy` | yes |
| `arn:aws:iam::111122223333:role/team.ci@deploy` | yes |
| `arn:aws:iam::1111:role/JenkinsDeploy` | no — account id must be 12 digits |
| `arn:aws:iam::111122223333:role/x; rm -rf /` | no |

## How it fits

Typically not called directly — [`withAwsCredentials`](withAwsCredentials.md)
calls it when `envCfg.assumeRole` or `cfg.infra.assumeRole` is set.

## Source

[`vars/assumeAwsRole.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/assumeAwsRole.groovy)
