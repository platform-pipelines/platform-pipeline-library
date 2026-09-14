# sonarProperties

Assembles the full `-D` flag list for `sonar-scanner`.

## Syntax

```groovy
sonarProperties(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

Reads `env.APP_VERSION`, `env.GIT_COMMIT`, `env.CHANGE_ID`, `env.CHANGE_BRANCH`,
`env.CHANGE_TARGET`, `env.BRANCH_NAME`.

### Config keys read

| Key | Default | Sample value | Sonar property |
|---|---|---|---|
| `quality.sonarProjectKey` | `appName` | `acme_orders-api` | `sonar.projectKey` |
| `appName` | — | `orders-api` | `sonar.projectName` |
| `quality.sonarSources` | `.` | `src` | `sonar.sources` |
| `quality.sonarExclusions` | `**/node_modules/**,**/target/**,**/build/**,**/dist/**` | `**/migrations/**` | `sonar.exclusions` |
| `buildTool` | — | `python` | coverage/report paths via [appSonarProps](appSonarProps.md) |

## Returns

A single `String` of space-separated `-Dkey=value` flags. Empty values are
left out. Branch builds send `sonar.branch.name`; PR builds send
`sonar.pullrequest.*` instead — never both, which Sonar rejects.

## Examples

**Branch build** (`BRANCH_NAME=main`):

```groovy
sonarProperties(cfg)
```

```
-Dsonar.projectKey=acme_orders-api -Dsonar.projectName=orders-api -Dsonar.projectVersion=1.4.0
-Dsonar.sources=. -Dsonar.exclusions=**/migrations/**,**/tests/**
-Dsonar.scm.revision=ab12cd3ef4567890ab12cd3ef4567890ab12cd3e
-Dsonar.branch.name=main -Dsonar.python.coverage.reportPaths=coverage.xml
```

(Shown wrapped; the real value is one line.)

**PR build** (`CHANGE_ID=87`, `CHANGE_BRANCH=feature/login`, `CHANGE_TARGET=main`):

```
... -Dsonar.pullrequest.key=87 -Dsonar.pullrequest.branch=feature/login -Dsonar.pullrequest.base=main ...
```

**Using it directly:**

```groovy
withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
    sh "sonar-scanner -Dsonar.host.url=${env.SONAR_HOST_URL} -Dsonar.token=\$SONAR_TOKEN ${sonarProperties(cfg)}"
}
```

!!! note "Values are not shell-quoted"
    Keep `sonarSources` and `sonarExclusions` free of spaces — each flag is
    passed as a single unquoted word.

## How it fits

Used by [scanSonar](scanSonar.md).

## Source

[`vars/sonarProperties.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/sonarProperties.groovy)
