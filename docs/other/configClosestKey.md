# configClosestKey

Returns the valid key a mistyped key was most likely meant to be, or `null`.
Uses case-insensitive edit distance with a cap, so an unrelated key is never
offered as a suggestion.

## Signature

```groovy
@NonCPS
def call(String key, List candidates)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `key` | `String` | The unknown key as written. |
| `candidates` | `List` | The keys that are valid at that position. |

## Returns

The closest candidate as a `String`, or `null` if none is within
`max(2, key.length() / 3)` edits.

## Usage

```groovy
configClosestKey('minCoverge', ['minCoverage', 'sonar'])   // 'minCoverage'
configClosestKey('kubernetes', ['sonar', 'trivy'])         // null
```

Used by [`configUnknownKeys`](configUnknownKeys.md).

## Source

[`vars/configClosestKey.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configClosestKey.groovy)
