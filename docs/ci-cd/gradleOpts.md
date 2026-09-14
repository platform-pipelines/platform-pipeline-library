# gradleOpts

Shared Gradle CLI flags used by every `gradle*` step. No daemon in CI: a
Gradle daemon survives between builds and leaks state across jobs, which is
exactly what you don't want on a shared agent.

## Syntax

```groovy
gradleOpts()
```

## Parameters

None.

## Returns

The `String` `--no-daemon --console=plain`.

| Flag | Why |
|---|---|
| `--no-daemon` | no state carried between builds on a shared agent |
| `--console=plain` | no progress-bar escape codes in the Jenkins log |

## Examples

```groovy
gradleOpts()                              // → '--no-daemon --console=plain'
sh "gradle ${gradleOpts()} test"          // gradle --no-daemon --console=plain test
sh "gradle ${gradleOpts()} dependencies --configuration runtimeClasspath"
```

## How it fits

Used by [gradleBuild](gradleBuild.md), [gradleLint](gradleLint.md),
[gradlePackage](gradlePackage.md), and [gradleTest](gradleTest.md).

## Source

[`vars/gradleOpts.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/gradleOpts.groovy)
