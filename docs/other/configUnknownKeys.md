# configUnknownKeys

Lists keys in a repo's config that the library does not recognise, each with a
"did you mean" hint when a valid key is close. Without it a typo such as
`minCoverge: 80` is silently ignored and the default applies, so the gate the
team thinks is on never runs.

## Syntax

```groovy
configUnknownKeys(Map raw)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `raw` | `Map` | yes | — | Config as the repo wrote it, **before** defaults are merged in. `null` is treated as `[:]`. |

## Returns

`List<String>` of warnings; empty when every key is recognised.

## What is checked

- Top-level keys, against [`configDefaults`](configDefaults.md).
- One level inside each section (`lint`, `quality`, `infra`, `publish`, ...).
- Keys of each `environments[]` entry, against [`configEnvDefaults`](configEnvDefaults.md).

Not checked: `extra` (free-form by design), values of free-form maps such as
`environments[].parameters`, and keys listed in
[`configDeprecatedKeys`](configDeprecatedKeys.md), which get their own warning.

## Examples

```yaml
# .ci/config.yaml
appName: orders-api
buildTool: python
imageRepo: ghcr.io/acme/orders-api
quality:
  minCoverge: 80          # typo
kubernetes: true          # not a key at all
environments:
  - name: prod
    approver: [jane.doe]  # should be approvers
extra:
  anything: goes          # never checked
```

```groovy
configUnknownKeys(readYaml(file: '.ci/config.yaml'))
// → [
//   "unknown config key 'quality.minCoverge' — did you mean 'minCoverage'?",
//   "unknown config key 'kubernetes' — it is ignored",
//   "unknown config key 'environments[0].approver' — did you mean 'approvers'?",
// ]

configUnknownKeys([appName: 'orders-api', buildTool: 'python'])   // → []
```

Logging them:

```groovy
configUnknownKeys(readYaml(file: '.ci/config.yaml')).each { logWarn it }
```

## How it fits

Called by [`configLoad`](configLoad.md), which logs each result as a warning.
Unknown keys never fail the build. Suggestions come from
[`configClosestKey`](configClosestKey.md).

## Source

[`vars/configUnknownKeys.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configUnknownKeys.groovy)
