# configClosestKey

Returns the valid key a mistyped key was most likely meant to be, or `null`.
Uses case-insensitive edit distance with a cap, so an unrelated key is never
offered as a suggestion — a wrong hint is worse than none.

## Syntax

```groovy
configClosestKey(String key, List candidates)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `key` | `String` | yes | — | The unknown key as written, e.g. `'minCoverge'`. |
| `candidates` | `List` | yes | — | The keys that are valid at that position, e.g. `configDefaults().quality.keySet()`. |

## Returns

The closest candidate as a `String`, or `null` if none is within
`max(2, key.length() / 3)` edits.

## Examples

```groovy
configClosestKey('minCoverge', ['minCoverage', 'sonar'])     // → 'minCoverage'   (1 edit)
configClosestKey('SONAR', ['sonar', 'trivy'])                // → 'sonar'         (case-insensitive)
configClosestKey('trivyFailon', ['trivyFailOn', 'trivy'])    // → 'trivyFailOn'
configClosestKey('kubernetes', ['sonar', 'trivy'])           // → null            (too far from anything)
```

Checking every key of a section:

```groovy
def valid = configDefaults().quality.keySet() as List
def hint  = configClosestKey('secretsScan', valid)           // → 'secretScan'
```

## How it fits

Used by [`configUnknownKeys`](configUnknownKeys.md) to add the
`did you mean '…'?` hint to unknown-key warnings. Marked `@NonCPS`: it is pure
string arithmetic with no pipeline steps.

## Source

[`vars/configClosestKey.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configClosestKey.groovy)
