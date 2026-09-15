# configGlobToRegex

Converts a `branchPattern` glob into a regex.

Only `*` is supported. Full regex in config invites patterns nobody can
reason about at 2am, and a wrong one silently deploys the wrong branch.

!!! note "`*` spans slashes"
    `feature/*` matches `feature/a/b` as well as `feature/a`. That's
    intentional — nested feature branches should reach the same environments
    as flat ones.

## Syntax

```groovy
configGlobToRegex(String glob)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `glob` | `String` | yes | — | A `branchPattern` using only `*` as a wildcard. `.` is matched literally. |

## Returns

A regex `String` equivalent to the glob: `.` is escaped and `*` becomes `.*`.

## Examples

| Glob | Regex | Matches | Does not match |
|---|---|---|---|
| `main` | `main` | `main` | `main-old` |
| `*` | `.*` | every branch | — |
| `release/*` | `release/.*` | `release/1.4`, `release/1.4/hotfix` | `releases/1.4` |
| `v1.*` | `v1\..*` | `v1.2` | `v1x2` |

```groovy
def pattern = configGlobToRegex('release/*')     // → 'release/.*'
'release/1.4' ==~ pattern                        // → true
'feature/x'   ==~ pattern                        // → false
```

## How it fits

Used by [`configEnvironmentsFor`](configEnvironmentsFor.md) to match a branch
name against each environment's `branchPattern`.

## Source

[`vars/configGlobToRegex.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configGlobToRegex.groovy)
