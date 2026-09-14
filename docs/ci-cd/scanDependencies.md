# scanDependencies

Runs OWASP Dependency-Check. Opt-in: it's slow and needs a warm NVD cache to
be tolerable, so most repos rely on [scanTrivy](scanTrivy.md) instead.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.appName` and `cfg.extra.dependencyCheckCvss` (default `7`). |

## Returns

Nothing. Archives the report and throws if a CVE at or above the CVSS
threshold is found.

## Usage

```groovy
scanDependencies(cfg)
```

Called from [qualityChecks](qualityChecks.md) when `cfg.quality.dependencyCheck`
is `true`.

## Source

[`vars/scanDependencies.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/scanDependencies.groovy)
