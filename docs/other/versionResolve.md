# versionResolve

Version derived from git, not from a Jenkins counter alone, so a version
string still traces to a commit after Jenkins is rebuilt from scratch.

| Branch | Version shape |
|---|---|
| `main` | `1.4.0` |
| `release/*` | `1.4.0-rc.42` |
| `hotfix/*` | `1.4.0-hotfix.42.gab12cd3` |
| `feature/*` (anything else) | `1.4.0-feature-login.42.gab12cd3` |

## Signature

```groovy
def call(String branch = null)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `branch` | `String` | Branch to resolve (default `env.BRANCH_NAME`). |

## Returns

A version string shaped by the branch pattern (see table above).

## Usage

```groovy
def version = versionResolve()
```

Combines [`versionBase`](versionBase.md), `env.BUILD_NUMBER`, and
[`versionShortSha`](versionShortSha.md); anything not matching `main`,
`release/*`, or `hotfix/*` is slugged via [`versionSlug`](versionSlug.md). See
[`versionImageTag`](versionImageTag.md) for the OCI-tag-safe variant.

## Source

[`vars/versionResolve.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/versionResolve.groovy)
