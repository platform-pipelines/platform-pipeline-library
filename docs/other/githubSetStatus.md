# githubSetStatus

Publishes a commit status so PR pages show pass/fail inline.

## Syntax

```groovy
githubSetStatus(String context, String state, String description)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `context` | `String` | yes | — | Status check name shown on the PR, e.g. `ci/sonar`. Same context = same row, updated in place. |
| `state` | `String` | yes | — | `pending`, `success`, `failure` or `error`. |
| `description` | `String` | yes | — | Short text next to the check; truncated to 140 characters. |

Also sends `env.BUILD_URL` as the status link, and posts against
`env.GIT_COMMIT` in [`githubRepoSlug`](githubRepoSlug.md).

## Returns

Nothing. No-op (debug log `No GIT_COMMIT — skipping commit status`) when
`env.GIT_COMMIT` is unset. A failed API call logs a warning but does not fail
the build.

## Examples

```groovy
githubSetStatus('ci/jenkins', 'pending', 'Pipeline running')
githubSetStatus('sonar', 'success', 'Quality gate passed')
githubSetStatus('ci/coverage', 'failure', '71.2% < 75%')
```

A custom check from your own stage:

```groovy
def status = sh(script: './scripts/contract-tests.sh', returnStatus: true)
githubSetStatus('ci/contract-tests',
                status == 0 ? 'success' : 'failure',
                status == 0 ? 'All consumer contracts pass' : 'Contract drift detected')
```

## Contexts the library uses

| Context | Set by |
|---|---|
| `ci/jenkins` | [initPipeline](../ci-cd/initPipeline.md), [standardPipeline](../ci-cd/standardPipeline.md) `post` |
| `ci/coverage` | [checkCoverage](../ci-cd/checkCoverage.md) |
| `ci/sonar` | [sonarWaitForGate](../ci-cd/sonarWaitForGate.md) |
| `ci/trivy-fs`, `ci/trivy-image` | [scanTrivy](../ci-cd/scanTrivy.md) |
| `ci/iac-scan` | [scanIac](../ci-cd/scanIac.md) |
| `ci/secrets` | [scanSecrets](../ci-cd/scanSecrets.md) |
| `ci/dependency-check` | [scanDependencies](../ci-cd/scanDependencies.md) |

## How it fits

Calls [`githubApiRequest`](githubApiRequest.md) against
`POST /repos/<slug>/statuses/<sha>`.

## Source

[`vars/githubSetStatus.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/githubSetStatus.groovy)
