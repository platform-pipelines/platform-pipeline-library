# configDeprecatedKeys

Config keys that moved, mapped from old path to new path. Renaming a key later
takes one new line here.

## Signature

```groovy
def call()
```

## Returns

`Map` of deprecated dotted key path → replacement dotted key path:

| Old key | New key |
|---|---|
| `extra.nexusRepo` | `publish.nexusRepo` |
| `extra.sonarSources` | `quality.sonarSources` |
| `extra.sonarExclusions` | `quality.sonarExclusions` |
| `extra.dependencyCheckCvss` | `quality.dependencyCheckCvss` |
| `extra.allowSelfApproval` | `approval.allowSelfApproval` |

## Usage

```groovy
configDeprecatedKeys().each { oldPath, newPath -> ... }
```

[`configLoad`](configLoad.md) copies an old key's value to its new key and logs
`extra.nexusRepo is deprecated — move it to publish.nexusRepo`. If a repo sets
both, the new key wins and the old one is reported as ignored. Existing
configs keep working while they migrate.

## Source

[`vars/configDeprecatedKeys.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configDeprecatedKeys.groovy)
