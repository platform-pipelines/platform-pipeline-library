# mavenOpts

Shared Maven CLI flags used by every `maven*` step. Batch mode and no
transfer progress keep logs readable; a workspace-local repo makes the
dependency cache mountable between builds.

## Signature

```groovy
def call()
```

## Returns

The shared Maven CLI flags as a `String`: `-B -ntp -Dmaven.repo.local=.m2`.

## Usage

```groovy
sh "mvn ${mavenOpts()} clean compile"
```

Used by [mavenBuild](mavenBuild.md), [mavenLint](mavenLint.md),
[mavenPackage](mavenPackage.md), and [mavenTest](mavenTest.md).

## Source

[`vars/mavenOpts.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/mavenOpts.groovy)
