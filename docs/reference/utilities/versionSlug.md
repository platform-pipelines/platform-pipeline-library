# versionSlug

Branch name to a token safe for versions and image tags.

!!! note "Why re-trim after truncation"
    Bounded to 30 characters so a long branch name doesn't produce an
    unreadable tag, then re-trimmed because truncation can land on a
    separator and a trailing dash is illegal in an OCI tag. A name made
    entirely of separators would otherwise yield an empty segment and
    produce a version like `1.4.0-.42.gabc123`, so that case falls back to
    `'branch'`.

## Syntax

```groovy
versionSlug(String s)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `s` | `String` | yes | — | Raw branch name. |

## Returns

Lowercase, with every run of non-alphanumeric characters turned into one `-`,
leading/trailing dashes removed, at most 30 characters; `'branch'` if nothing
is left.

## Examples

| Input | Result |
|---|---|
| `feature/Add_Login-Page` | `feature-add-login-page` |
| `PR-87` | `pr-87` |
| `jane/JIRA-1234--fix` | `jane-jira-1234-fix` |
| `feature/a-really-long-branch-name-for-testing` | `feature-a-really-long-branch-n` |
| `///` | `branch` |

```groovy
def slug = versionSlug('feature/Add_Login-Page')    // → 'feature-add-login-page'
```

## How it fits

Called by [`versionResolve`](versionResolve.md) for any branch that isn't
`main`, `master`, `release/*`, or `hotfix/*`.

## Source

[`vars/versionSlug.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/versionSlug.groovy)
