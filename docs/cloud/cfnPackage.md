# cfnPackage

Runs `aws cloudformation package`, which uploads local artifacts (Lambda
zips, nested stack bodies) to S3 and rewrites the template to point at them.
Skipped when no `artifactBucket` is configured, since templates with no
local references don't need it.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.infra.template`/`artifactBucket`, `cfg.appName`. |

## Returns

Nothing — writes and archives `packaged-template.yaml`, or skips entirely if
`infra.artifactBucket` is unset.

## Usage

```groovy
cfnPackage(cfg)
```

If `packaged-template.yaml` exists, [cfnChangeSet](cfnChangeSet.md) uses it
in place of `cfg.infra.template`.

## Source

[`vars/cfnPackage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/cfnPackage.groovy)
