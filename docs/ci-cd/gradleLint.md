# gradleLint

Runs Checkstyle and SpotBugs against a Gradle project.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.lint.autoFormat` and `cfg.lint.failOnError`. |

## Returns

Nothing — errors if Checkstyle or SpotBugs report violations and
`failOnError` is true.

## Usage

```groovy
gradleLint(cfg)
```

Called by [lintApp](lintApp.md) when `cfg.buildTool == 'gradle'`. Uses the
shared flags from [gradleOpts](gradleOpts.md).

## Source

[`vars/gradleLint.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/gradleLint.groovy)
