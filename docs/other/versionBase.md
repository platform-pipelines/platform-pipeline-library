# versionBase

Nearest semver tag, normalised. Falls back to `0.1.0` for a repo with no tags
yet so a first build still produces a usable version.

## Signature

```groovy
def call()
```

## Returns

Nearest tag as `"X.Y.Z"` (v-prefix stripped), or `'0.1.0'` if none or
unparseable.

## Usage

```groovy
def base = versionBase()
```

Called by [`versionResolve`](versionResolve.md) as the starting point for
every version string it builds.

## Source

[`vars/versionBase.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/versionBase.groovy)
