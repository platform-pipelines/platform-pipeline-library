# withAwsCredentials

Wraps a body with AWS credentials for the target environment. Prefers
per-environment role assumption over static keys: a single set of
long-lived keys that can reach production is the thing you least want on a
build agent.

## Signature

```groovy
def call(Map cfg, Map envCfg = [:], Closure body)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.infra.region`/`assumeRole`/`awsCredentialsId`. |
| `envCfg` | `Map` | Target environment config; overrides `cfg`'s region/assumeRole/awsCredentialsId (default `[:]`). |
| `body` | `Closure` | Code to run with AWS credentials (and, if a role is set, an assumed role) exported. |

## Returns

Nothing — runs `body()` inside the credential scope.

## Usage

```groovy
withAwsCredentials(cfg, envCfg) {
    sh 'aws s3 ls'
}
```

If `envCfg.assumeRole` or `cfg.infra.assumeRole` is set, delegates to
[assumeAwsRole](assumeAwsRole.md) inside the static-credential scope.
Used by every `cfn*` build/deploy step that talks to AWS.

## Source

[`vars/withAwsCredentials.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/withAwsCredentials.groovy)
