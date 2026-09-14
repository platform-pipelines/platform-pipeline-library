# configUnknownKeys

Lists keys in a repo's config that the library does not recognise, each with a
"did you mean" hint when a valid key is close. Without it a typo such as
`minCoverge: 80` is silently ignored and the default applies, so the gate the
team thinks is on never runs.

## Signature

```groovy
def call(Map raw)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `raw` | `Map` | Config as the repo wrote it, before defaults are merged in. |

## Returns

`List<String>` of warnings, e.g.
`unknown config key 'quality.minCoverge' — did you mean 'minCoverage'?`.
Empty when every key is recognised.

## What is checked

- Top-level keys, against [`configDefaults`](configDefaults.md).
- One level inside each section (`lint`, `quality`, `infra`, `publish`, ...).
- Keys of each `environments[]` entry, against [`configEnvDefaults`](configEnvDefaults.md).

Not checked: `extra` (free-form by design), values of free-form maps such as
`environments[].parameters`, and keys listed in
[`configDeprecatedKeys`](configDeprecatedKeys.md), which get their own warning.

## Usage

```groovy
configUnknownKeys(readYaml(file: '.ci/config.yaml')).each { logWarn it }
```

Called by [`configLoad`](configLoad.md), which logs each result as a warning.
Unknown keys never fail the build. Suggestions come from
[`configClosestKey`](configClosestKey.md).

## Source

[`vars/configUnknownKeys.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configUnknownKeys.groovy)
