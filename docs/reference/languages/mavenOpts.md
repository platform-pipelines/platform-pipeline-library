# mavenOpts

Shared Maven CLI flags used by every `maven*` step. Batch mode and no
transfer progress keep logs readable; a workspace-local repo makes the
dependency cache mountable between builds.

## Syntax

```groovy
mavenOpts()
```

## Parameters

None.

## Returns

The `String` `-B -ntp -Dmaven.repo.local=.m2`.

| Flag | Why |
|---|---|
| `-B` | batch mode — no interactive prompts, no colour codes |
| `-ntp` | no download-progress lines flooding the log |
| `-Dmaven.repo.local=.m2` | dependencies cached in the workspace (see [appCacheDir](../pipelines/appCacheDir.md)) |

## Examples

```groovy
mavenOpts()                                           // → '-B -ntp -Dmaven.repo.local=.m2'
sh "mvn ${mavenOpts()} clean compile"
sh "mvn ${mavenOpts()} dependency:tree -Dincludes=org.slf4j"
```

## How it fits

Used by [mavenBuild](mavenBuild.md), [mavenLint](mavenLint.md),
[mavenPackage](mavenPackage.md), and [mavenTest](mavenTest.md).

## Source

[`vars/mavenOpts.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/mavenOpts.groovy)
