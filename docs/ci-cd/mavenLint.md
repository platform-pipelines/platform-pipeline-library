# mavenLint

Runs Checkstyle and SpotBugs against a Maven project. Both write XML that
Jenkins can surface, and both run offline once the plugin jars are cached.

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
mavenLint(cfg)
```

Called by [lintApp](lintApp.md) when `cfg.buildTool == 'maven'`. Uses the
shared flags from [mavenOpts](mavenOpts.md).

## Source

[`vars/mavenLint.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/mavenLint.groovy)
