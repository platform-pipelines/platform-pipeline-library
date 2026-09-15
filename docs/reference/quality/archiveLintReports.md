# archiveLintReports

Archives whatever lint report this toolchain produced.

Centralized so each lint step only has to know how to run its linter, not
how Jenkins stores artifacts.

## Syntax

```groovy
archiveLintReports(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; passed to [`appLintReport(cfg)`](../pipelines/appLintReport.md). |

## Returns

Nothing. Archives the report glob if the toolchain has one; a missing file is
not an error (`allowEmptyArchive: true`).

## Examples

```groovy
archiveLintReports([buildTool: 'go'])       // archives golangci-report.xml
archiveLintReports([buildTool: 'docker-only'])   // does nothing
```

Archive even when lint fails:

```groovy
stage('Lint') {
    steps  { script { lintApp(cfg) } }
    post   { always { script { archiveLintReports(cfg) } } }
}
```

## How it fits

Called from the `post { always }` of [standardPipeline](../pipelines/standardPipeline.md)'s
`Lint` stage.

## Source

[`vars/archiveLintReports.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/archiveLintReports.groovy)
