# versionResolve

Version derived from git, not from a Jenkins counter alone, so a version
string still traces to a commit after Jenkins is rebuilt from scratch.

## Syntax

```groovy
versionResolve()                   // uses env.BRANCH_NAME
versionResolve(String branch)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `branch` | `String` | no | `env.BRANCH_NAME` | Branch to resolve. `null` with no `BRANCH_NAME` is treated as `detached`. |

Also reads `env.BUILD_NUMBER` (default `0`), [`versionBase`](versionBase.md)
and [`versionShortSha`](versionShortSha.md).

## Returns

A version string shaped by the branch:

| Branch | Shape | Example (`base=1.4.0`, build `42`, sha `ab12cd3`) |
|---|---|---|
| `main`, `master` | `<base>` | `1.4.0` |
| `release/*` | `<base>-rc.<build>` | `1.4.0-rc.42` |
| `hotfix/*` | `<base>-hotfix.<build>.g<sha>` | `1.4.0-hotfix.42.gab12cd3` |
| anything else | `<base>-<slug>.<build>.g<sha>` | `feature/login` → `1.4.0-feature-login.42.gab12cd3` |

`<slug>` comes from [`versionSlug`](versionSlug.md).

## Examples

```groovy
versionResolve()                    // on main → '1.4.0'
versionResolve('release/1.4')       // → '1.4.0-rc.42'
versionResolve('hotfix/CVE-2026')   // → '1.4.0-hotfix.42.gab12cd3'
versionResolve('PR-87')             // → '1.4.0-pr-87.42.gab12cd3'
```

```groovy
env.APP_VERSION = versionResolve(env.BRANCH_NAME)
sh "mvn -B package -Drevision=${env.APP_VERSION}"
```

## How it fits

[initPipeline](../ci-cd/initPipeline.md) stores it as `env.APP_VERSION`. See
[`versionImageTag`](versionImageTag.md) for the OCI-tag-safe variant.

## Source

[`vars/versionResolve.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/versionResolve.groovy)
