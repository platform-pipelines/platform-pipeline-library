# mavenLint

Runs Checkstyle and SpotBugs against a Maven project. Both write XML that
Jenkins can surface, and both run offline once the plugin jars are cached.

## Syntax

```groovy
mavenLint(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `lint.autoFormat` | `false` | `true` | Runs `spotless:apply` first (its failure is ignored). |
| `lint.failOnError` | `true` | `false` | `false` = violations are reported, not fatal. |

## Returns

Nothing. Fails the build with `Checkstyle or SpotBugs reported violations`
when either check fails and `failOnError` is `true`.

## Examples

```yaml
buildTool: maven
lint:
  failOnError: true
```

```groovy
mavenLint(cfg)
```

Runs:

```bash
mvn -B -ntp -Dmaven.repo.local=.m2 checkstyle:check spotbugs:check
# with lint.autoFormat: true, first:
mvn -B -ntp -Dmaven.repo.local=.m2 spotless:apply || true
```

The goals resolve by plugin prefix, so declare the plugins in `pom.xml`:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-checkstyle-plugin</artifactId>
      <version>3.6.0</version>
      <configuration><configLocation>google_checks.xml</configLocation></configuration>
    </plugin>
    <plugin>
      <groupId>com.github.spotbugs</groupId>
      <artifactId>spotbugs-maven-plugin</artifactId>
      <version>4.9.3.0</version>
    </plugin>
  </plugins>
</build>
```

## How it fits

Called by [lintApp](lintApp.md) when `cfg.buildTool == 'maven'`.
`target/checkstyle-result.xml` is archived by
[archiveLintReports](archiveLintReports.md).

## Source

[`vars/mavenLint.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/mavenLint.groovy)
