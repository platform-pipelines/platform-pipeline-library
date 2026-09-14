# logBanner

Visual stage separator. Makes long console logs scannable.

## Syntax

```groovy
logBanner 'Title'
logBanner(String title)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `title` | `String` | yes | — | Text to print between two 68-character rule lines. |

## Returns

Nothing — prints the banner to the build log.

## Examples

```groovy
logBanner 'Initialise'
logBanner "Deploy -> ${envCfg.name}"
```

Output:

```
====================================================================
  Deploy -> prod
====================================================================
```

## How it fits

Every library step that does real work opens with one (`Lint: Go`,
`Build: Python`, `Trivy fs: .`, `Plan: prod`, …), so you can search the log
for a stage by its banner.

## Source

[`vars/logBanner.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/logBanner.groovy)
