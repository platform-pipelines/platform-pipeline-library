# sonarProperties

Assembles the full `-D` flag list for `sonar-scanner`.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.appName` and `cfg.quality.sonarProjectKey` / `sonarSources` (default `.`) / `sonarExclusions`. |

## Returns

Space-separated `-Dkey=value` flags, including PR-analysis or branch-analysis
parameters.

## Usage

```groovy
sh "sonar-scanner ${sonarProperties(cfg)}"
```

!!! note
    PR analysis (`sonar.pullrequest.*`) and branch analysis
    (`sonar.branch.name`) are mutually exclusive in Sonar — this picks one
    based on `env.CHANGE_ID`, never sends both.

Merges in build-tool-specific coverage/report paths from
[appSonarProps](appSonarProps.md). Used by [scanSonar](scanSonar.md).

## Source

[`vars/sonarProperties.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/sonarProperties.groovy)
