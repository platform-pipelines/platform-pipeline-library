# versionSlug

Branch name to a token safe for versions and image tags.

!!! note "Why re-trim after truncation"
    Bounded to 30 characters so a long branch name doesn't produce an
    unreadable tag, then re-trimmed because truncation can land on a
    separator and a trailing dash is illegal in an OCI tag. A name made
    entirely of separators would otherwise yield an empty segment and
    produce a version like `1.4.0-.42.gabc123`, so that case falls back to
    `'branch'`.

## Signature

```groovy
def call(String s)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `s` | `String` | Raw branch name. |

## Returns

Lowercase, dash-separated, ≤30 char slug; `'branch'` if the input is all
separators.

## Usage

```groovy
def slug = versionSlug('feature/Add_Login-Page')
```

Called by [`versionResolve`](versionResolve.md) for any branch that isn't
`main`, `release/*`, or `hotfix/*`.

## Source

[`vars/versionSlug.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/versionSlug.groovy)
