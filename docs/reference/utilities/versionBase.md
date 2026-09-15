# versionBase

Nearest semver tag, normalised. Falls back to `0.1.0` for a repo with no tags
yet so a first build still produces a usable version.

## Syntax

```groovy
versionBase()
```

## Parameters

None. Runs `git describe --tags --abbrev=0` in the workspace, so the checkout
must include tags.

## Returns

The nearest tag reachable from `HEAD` as `X.Y.Z` (a leading `v` is
stripped). Falls back to `0.1.0` when there is no tag or the tag isn't plain
`X.Y.Z`.

## Examples

| Nearest tag | Result |
|---|---|
| `v1.4.0` | `1.4.0` |
| `2.0.3` | `2.0.3` |
| `v1.4.0-rc.1` | `0.1.0` (not plain `X.Y.Z`) |
| `release-7` | `0.1.0` |
| no tags | `0.1.0` |

```groovy
def base = versionBase()      // → '1.4.0'
```

Cutting the next minor release makes every later build pick it up:

```bash
git tag v1.5.0 && git push origin v1.5.0
```

## How it fits

Called by [`versionResolve`](versionResolve.md) as the starting point for
every version string it builds.

## Source

[`vars/versionBase.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/versionBase.groovy)
