# scanSonar

Runs SonarQube analysis, then waits for the quality gate if the config
demands it.

## Syntax

```groovy
scanSonar(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `quality.sonar` | `true` | `false` | Turns the check on (read by [qualityChecks](qualityChecks.md)). |
| `quality.failOnQualityGate` | `true` | `false` | `true` = wait for the gate and fail the build if it isn't `OK`. |
| `quality.sonarProjectKey` | `appName` | `acme_orders-api` | via [sonarProperties](sonarProperties.md) |
| `quality.sonarSources` | `.` | `src` | via [sonarProperties](sonarProperties.md) |
| `quality.sonarExclusions` | `**/node_modules/**,**/target/**,**/build/**,**/dist/**` | `**/migrations/**,**/tests/**` | via [sonarProperties](sonarProperties.md) |

### Controller requirements

| Item | Kind | Sample value |
|---|---|---|
| `SONAR_HOST_URL` | environment variable | `https://sonar.acme.internal` |
| `sonar-token` | string credential | a SonarQube analysis token |

## Returns

Nothing. Fails the build if the scanner fails, or (with
`failOnQualityGate: true`) if the gate result isn't `OK`.

## Examples

```yaml
# .ci/config.yaml — from examples/python-service
quality:
  sonar: true
  sonarProjectKey: acme_orders-api
  sonarExclusions: "**/migrations/**,**/tests/**"
  failOnQualityGate: true
```

```groovy
scanSonar(cfg)
```

Runs (on `main`):

```bash
sonar-scanner -Dsonar.host.url=https://sonar.acme.internal -Dsonar.token=$SONAR_TOKEN \
  -Dsonar.projectKey=acme_orders-api -Dsonar.projectName=orders-api -Dsonar.projectVersion=1.4.0 \
  -Dsonar.sources=. -Dsonar.exclusions=**/migrations/**,**/tests/** \
  -Dsonar.scm.revision=ab12cd3e… -Dsonar.branch.name=main \
  -Dsonar.python.coverage.reportPaths=coverage.xml
```

Report the analysis without blocking the build:

```yaml
quality:
  failOnQualityGate: false
```

## How it fits

Flags come from [sonarProperties](sonarProperties.md); the gate wait is
[sonarWaitForGate](sonarWaitForGate.md). Called from
[qualityChecks](qualityChecks.md) when `cfg.quality.sonar` is `true`.

## Source

[`vars/scanSonar.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/scanSonar.groovy)
