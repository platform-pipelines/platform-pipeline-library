# versionImageTag

Same value as [`versionResolve`](versionResolve.md), constrained to
characters legal in an OCI tag.

## Signature

```groovy
def call(String branch = null)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `branch` | `String` | Branch to resolve (default `env.BRANCH_NAME`). |

## Returns

[`versionResolve`](versionResolve.md)'s value with any illegal OCI-tag
character replaced by `-`.

## Usage

```groovy
def tag = versionImageTag()
```

## Source

[`vars/versionImageTag.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/versionImageTag.groovy)
