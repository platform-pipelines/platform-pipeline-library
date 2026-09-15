# Languages

One `*Lint` / `*Build` / `*Test` / `*Package` step per supported `buildTool`.
You rarely call them directly:
[lintApp](../pipelines/lintApp.md), [buildApp](../pipelines/buildApp.md),
[testApp](../pipelines/testApp.md) and [packageApp](../pipelines/packageApp.md)
dispatch to them. Terraform and CloudFormation steps are under
[Infrastructure](../infrastructure/index.md).

| Tool (`buildTool`) | Lint | Build | Test | Package |
|---|---|---|---|---|
| Go (`go`) | [goLint](goLint.md) | [goBuild](goBuild.md) | [goTest](goTest.md) | [goPackage](goPackage.md) |
| Gradle (`gradle`) | [gradleLint](gradleLint.md) | [gradleBuild](gradleBuild.md) | [gradleTest](gradleTest.md) | [gradlePackage](gradlePackage.md) |
| Maven (`maven`) | [mavenLint](mavenLint.md) | [mavenBuild](mavenBuild.md) | [mavenTest](mavenTest.md) | [mavenPackage](mavenPackage.md) |
| Node (`npm`) | [nodeLint](nodeLint.md) | [nodeBuild](nodeBuild.md) | [nodeTest](nodeTest.md) | [nodePackage](nodePackage.md) |
| Python (`python`) | [pythonLint](pythonLint.md) | [pythonBuild](pythonBuild.md) | [pythonTest](pythonTest.md) | [pythonPackage](pythonPackage.md) |
| docker-only (`docker-only`) | [dockerOnlyLint](dockerOnlyLint.md) | [dockerOnlyBuild](dockerOnlyBuild.md) | [dockerOnlyTest](dockerOnlyTest.md) | [dockerOnlyPackage](dockerOnlyPackage.md) |

Helpers: [gradleOpts](gradleOpts.md), [mavenOpts](mavenOpts.md),
[nodeInstall](nodeInstall.md), [pythonVenv](pythonVenv.md).

All language steps share these lint keys:

```yaml
lint:
  enabled: true        # false skips the Lint stage's work
  failOnError: true    # false = report-only
  autoFormat: false    # true = run the formatter's fix mode first
```
