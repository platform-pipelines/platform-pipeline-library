# gradleOpts

Shared Gradle CLI flags used by every `gradle*` step. No daemon in CI: a
Gradle daemon survives between builds and leaks state across jobs, which is
exactly what you don't want on a shared agent.

## Signature

```groovy
def call()
```

## Returns

The shared Gradle CLI flags as a `String`: `--no-daemon --console=plain`.

## Usage

```groovy
sh "gradle ${gradleOpts()} test"
```

Used by [gradleBuild](gradleBuild.md), [gradleLint](gradleLint.md),
[gradlePackage](gradlePackage.md), and [gradleTest](gradleTest.md).

## Source

[`vars/gradleOpts.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/gradleOpts.groovy)
