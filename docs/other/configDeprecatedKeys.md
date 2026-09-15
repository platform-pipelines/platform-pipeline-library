# configDeprecatedKeys

Config keys that moved, mapped from old path to new path. Renaming a key later
takes one new line here.

## Syntax

```groovy
configDeprecatedKeys()
```

## Parameters

None.

## Returns

`Map<String, String>` of deprecated dotted key path → replacement dotted key path:

| Old key | New key |
|---|---|
| `extra.sonarSources` | `quality.sonarSources` |
| `extra.sonarExclusions` | `quality.sonarExclusions` |
| `extra.dependencyCheckCvss` | `quality.dependencyCheckCvss` |
| `extra.allowSelfApproval` | `approval.allowSelfApproval` |

## Examples

```groovy
configDeprecatedKeys()['extra.sonarSources']       // → 'quality.sonarSources'

configDeprecatedKeys().each { oldPath, newPath ->
    echo "${oldPath} → ${newPath}"
}
```

A repo still using an old key:

```yaml
# .ci/config.yaml (before)
extra:
  sonarSources: src
```

keeps working — [`configLoad`](configLoad.md) copies the value across and logs:

```
[WARN]  extra.sonarSources is deprecated — move it to quality.sonarSources
```

The migrated config:

```yaml
# .ci/config.yaml (after)
quality:
  sonarSources: src
```

If both are set, the new key wins and the build logs
`extra.sonarSources is ignored because quality.sonarSources is also set — remove extra.sonarSources`.

## How it fits

Read by [`configLoad`](configLoad.md) (to migrate values) and
[`configUnknownKeys`](configUnknownKeys.md) (so old keys get the deprecation
warning instead of an "unknown key" warning).

## Source

[`vars/configDeprecatedKeys.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configDeprecatedKeys.groovy)
