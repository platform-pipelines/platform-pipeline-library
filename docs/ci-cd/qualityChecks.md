# qualityChecks

Builds the map of quality gates that can run at the same time, keyed by
check name, for [`parallel()`](https://www.jenkins.io/doc/pipeline/steps/workflow-basic-steps/#parallel-execute-in-parallel).

`failFast` is deliberately off: seeing every problem in one run beats fixing
them one build at a time.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.quality.*` to decide which gates apply. |

## Returns

A `Map` of check name → `Closure`, suitable for `parallel()`, plus
`failFast: false`.

## Usage

```groovy
parallel(qualityChecks(cfg))
```

Dispatches to [scanSonar](scanSonar.md), [scanTrivy](scanTrivy.md) (or
[scanIac](scanIac.md) for infra repos), [scanSecrets](scanSecrets.md), and
[scanDependencies](scanDependencies.md) depending on which `cfg.quality`
flags are enabled.

## Source

[`vars/qualityChecks.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/qualityChecks.groovy)
