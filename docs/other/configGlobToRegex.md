# configGlobToRegex

Converts a `branchPattern` glob into a regex.

Only `*` is supported. Full regex in config invites patterns nobody can
reason about at 2am, and a wrong one silently deploys the wrong branch.

!!! note "`*` spans slashes"
    `feature/*` matches `feature/a/b` as well as `feature/a`. That's
    intentional — nested feature branches should reach the same environments
    as flat ones.

## Signature

```groovy
def call(String glob)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `glob` | `String` | A `branchPattern` using only `*` as a wildcard. |

## Returns

Regex `String` equivalent to the glob.

## Usage

```groovy
def pattern = configGlobToRegex('release/*')
```

Used by [`configEnvironmentsFor`](configEnvironmentsFor.md) to match a branch
name against each environment's `branchPattern`.

## Source

[`vars/configGlobToRegex.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configGlobToRegex.groovy)
