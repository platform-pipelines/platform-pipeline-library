# withAwsCredentials

Wraps a body with AWS credentials for the target environment. Prefers
per-environment role assumption over static keys: a single set of
long-lived keys that can reach production is the thing you least want on a
build agent.

## Syntax

```groovy
withAwsCredentials(Map cfg) {
    // steps
}

withAwsCredentials(Map cfg, Map envCfg) {
    // steps
}
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |
| `envCfg` | `Map` | no | `[:]` | Target environment; its values win over `cfg.infra`'s. |
| `body` | `Closure` | yes | — | Steps to run with AWS access. |

### Config keys read

Each value is resolved environment first, then `infra`, then the built-in
default:

| Setting | Environment key | Infra key | Default | Sample value |
|---|---|---|---|---|
| Region | `environments[].region` | `infra.region` | `us-east-1` (from defaults) | `eu-west-1` |
| Base credential | `environments[].awsCredentialsId` | `infra.awsCredentialsId` | `aws-credentials` | `aws-prod-credentials` |
| Role to assume | `environments[].assumeRole` | `infra.assumeRole` | none | `arn:aws:iam::111122223333:role/JenkinsDeploy` |

The base credential is a Jenkins **username/password** credential: username =
access key id, password = secret access key.

## Returns

Nothing. Inside `body`:

| Variable | Value |
|---|---|
| `AWS_REGION`, `AWS_DEFAULT_REGION` | resolved region |
| `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` | from the base credential, or the assumed role |
| `AWS_SESSION_TOKEN` | only when a role is assumed |

## Examples

**Static keys, shared settings:**

```yaml
infra:
  region: us-east-1
  awsCredentialsId: aws-credentials
```

```groovy
withAwsCredentials(cfg) {
    sh 'aws cloudformation list-stacks --max-items 5'
}
```

**Per-environment role (from examples/terraform-stack):**

```yaml
infra:
  region: eu-west-1
environments:
  - name: dev
  - name: prod
    assumeRole: arn:aws:iam::111122223333:role/JenkinsDeploy
```

```groovy
def prod = cfg.environments.find { it.name == 'prod' }

withAwsCredentials(cfg, prod) {
    sh 'aws sts get-caller-identity --query Arn --output text'
    // → arn:aws:sts::111122223333:assumed-role/JenkinsDeploy/jenkins-42-ab12cd3
}
```

**Different account and region for one environment:**

```yaml
environments:
  - name: dr
    region: us-west-2
    awsCredentialsId: aws-dr-credentials
```

## How it fits

If a role is set, delegates to [assumeAwsRole](assumeAwsRole.md) inside the
static-credential scope. Used by every `cfn*` step that talks to AWS
([cfnBuild](cfnBuild.md), [cfnPackage](cfnPackage.md),
[cfnChangeSet](cfnChangeSet.md), [cfnDeploy](cfnDeploy.md)).

!!! note "Terraform steps"
    The Terraform steps don't call `withAwsCredentials` themselves — the
    agent (or a wrapping block in your Jenkinsfile) must provide AWS
    credentials for providers and S3 backends.

## Source

[`vars/withAwsCredentials.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/withAwsCredentials.groovy)
