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

## Signature

```groovy
def call(String roleArn, Closure body)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `roleArn` | `String` | IAM role ARN to assume; must match `arn:aws:iam::<12 digits>:role/<name>`. |
| `body` | `Closure` | Code to run with `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, and `AWS_SESSION_TOKEN` exported for the assumed role. |

## Returns

Nothing. Throws if `roleArn` fails validation, or if `aws sts assume-role`
doesn't return exactly three credential fields.

## Usage

```groovy
assumeAwsRole('arn:aws:iam::123456789012:role/deploy') {
    sh 'aws s3 ls'
}
```

Typically not called directly — [`withAwsCredentials`](withAwsCredentials.md)
calls it when `cfg.infra.assumeRole` (or `envCfg.assumeRole`) is set.

## Source

[`vars/assumeAwsRole.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/assumeAwsRole.groovy)
