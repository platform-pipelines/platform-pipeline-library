# qualityChecks

Builds the map of quality gates that can run at the same time, keyed by
check name, for [`parallel()`](https://www.jenkins.io/doc/pipeline/steps/workflow-basic-steps/#parallel-execute-in-parallel).

`failFast` is deliberately off: seeing every problem in one run beats fixing
them one build at a time.

## Syntax

```groovy
parallel qualityChecks(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

### Config keys read

| Key | Default | Adds branch | Runs |
|---|---|---|---|
| `quality.sonar` | `true` | `sonarqube` | [scanSonar](scanSonar.md) |
| `quality.trivy` | `true` | `trivy-fs` (apps) or `iac-scan` (infra) | [scanTrivy](scanTrivy.md) `type: 'fs'` / [scanIac](scanIac.md) |
| `quality.secretScan` | `true` | `secrets` | [scanSecrets](scanSecrets.md) |
| `quality.dependencyCheck` | `false` | `dependency-check` | [scanDependencies](scanDependencies.md) |

## Returns

A `Map` of branch name → `Closure`, plus `failFast: false`. With every flag
off it contains only `failFast`.

## Examples

With the defaults on an application repo:

```groovy
qualityChecks(cfg).keySet()
// → ['sonarqube', 'trivy-fs', 'secrets', 'failFast']
```

With OWASP Dependency-Check turned on and Sonar off:

```yaml
quality:
  sonar: false
  dependencyCheck: true
  dependencyCheckCvss: 8
```

```groovy
qualityChecks(cfg).keySet()
// → ['trivy-fs', 'secrets', 'dependency-check', 'failFast']
```

Infra repo (`buildTool: terraform`):

```groovy
qualityChecks(cfg).keySet()
// → ['sonarqube', 'iac-scan', 'secrets', 'failFast']
```

Adding your own gate alongside the standard ones:

```groovy
def checks = qualityChecks(cfg)
checks['licenses'] = { sh './scripts/check-licenses.sh' }
parallel checks
```

## How it fits

Called from [standardPipeline](standardPipeline.md)'s `Quality & Security`
stage.

## Source

[`vars/qualityChecks.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/qualityChecks.groovy)
