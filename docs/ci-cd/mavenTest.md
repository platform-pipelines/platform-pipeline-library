# mavenTest

Runs `mvn verify` (not `test`) so Surefire and the JaCoCo report goal both
run — the coverage gate and Sonar scan both depend on the JaCoCo output.

## Syntax

```groovy
mavenTest(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; unused, kept so every `*Test` step has the same signature. |

## Returns

Nothing. Fails the build on a test failure. Produces:

| File | Used by |
|---|---|
| `target/surefire-reports/*.xml` | JUnit results in Jenkins, Sonar |
| `target/site/jacoco/jacoco.xml` | [checkCoverage](checkCoverage.md), Sonar |

## Examples

```groovy
mavenTest(cfg)
```

Runs:

```bash
mvn -B -ntp -Dmaven.repo.local=.m2 verify -DskipITs
```

Bind JaCoCo's `report` goal so `verify` writes the XML:

```xml
<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.13</version>
  <executions>
    <execution><goals><goal>prepare-agent</goal></goals></execution>
    <execution><id>report</id><phase>verify</phase><goals><goal>report</goal></goals></execution>
  </executions>
</plugin>
```

## How it fits

Called by [testApp](testApp.md) when `cfg.buildTool == 'maven'`.

## Source

[`vars/mavenTest.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/mavenTest.groovy)
