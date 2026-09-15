# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

`platform-pipeline` is a Jenkins shared library. Consuming repos call one entry point (`standardPipeline()`, `cdPipeline()`, or `terraformDriftPipeline()`) and put everything else in `.ci/config.yaml`. `README.md` is the full reference for config keys, credentials, and design rationale.

## Commands

Requires JDK 17. Groovy 3.0.19 can't read newer class files. On macOS the Makefile sets `JAVA_HOME` through `/usr/libexec/java_home -v 17`. Anywhere else, set `JAVA_HOME` yourself before running `./gradlew`.

```bash
make check                                   # test + lint, same as CI (.github/workflows/ci.yml)
make test                                    # ./gradlew test
make lint                                    # ./gradlew codenarcMain codenarcTest
./gradlew test --tests CfnChangeSetInjectionTest            # one test class
./gradlew test --tests 'VersionSlugTest.some test name*'    # one test method (names are quoted strings)
```

- CodeNarc (`config/codenarc/codenarc.groovy`) allows zero priority-1 or priority-2 violations. It checks `vars/` and `test/`.
- Test reports go to `build/reports/tests/test` and CodeNarc reports go to `build/reports/codenarc`.
- Python resource scripts run on their own: `python3 resources/com/platformpipelines/scripts/coverage_percent.py coverage.xml`.
- Docs: `make docs-serve` / `make docs-build` (mkdocs-material in `.venv-docs`, installed with uv). `site/` is build output.
- Toolbox image: `make toolbox-verify` (amd64 only). Run it after changing any tool version in `toolbox/Dockerfile`.
- Local Jenkins, SonarQube, and Nexus: `make local-up` / `make local-down` (uses `local/docker-compose.yml` and `local/casc/jenkins.yaml`).

## Architecture

**Everything is a `vars/` script. There is no `src/`.** Each `vars/<name>.groovy` defines exactly one `call()`, and the filename is the step name. This avoids `NotSerializableException` from CPS serialization. Do not add `src/` classes or helper methods inside a vars file. Make a new step instead. Each step starts with a header comment in the format `// Usage:`, `// Params:`, `// Returns:`.

**Config flow.** `initPipeline` → `configLoad` reads `.ci/config.yaml`, then `configMerge` layers it over `configDefaults` and `configEnvDefaults`. It also applies the `configDeprecatedKeys` migrations and runs `configUnknownKeys` (warns, with a `configClosestKey` suggestion) and `configValidate` (fails and lists every problem). The result is a plain `Map cfg` that every other step reads. **`configDefaults` / `configEnvDefaults` are the schema.** A new key must go there, even with a `null` default, or it gets reported as unknown. Allowed values each live in one place: `configSupportedTools`, `configImageBuilders`, `configDeployStrategies`.

**Dispatch by `cfg.buildTool`.** `lintApp`, `buildApp`, `testApp`, and `packageApp` route to per-language steps (`go*`, `python*`, `maven*`, `gradle*`, `node*`, `dockerOnly*`, `terraform*`, `cfn*`). The `app*` metadata steps (`appToolImage`, `appTestReport`, `appCoverageFile`, `appArtifacts`, `appCacheDir`, `appLintReport`, `appSonarProps`) switch on the same value. Unknown values must fail loudly. To add a language, write its four steps, add a case to every dispatcher and every `app*` step, and add it to `configSupportedTools`. `AppMetadataTest` enforces the metadata cases.

**Infra mode.** `buildTool: terraform|cloudformation` (`isInfraRepo`) derives `containerize: false` and the deploy strategy, and skips SBOM, signing, and publish. The key invariant: **apply consumes the exact approved plan.** Terraform applies the saved `tfplan`. CloudFormation executes the change set created before the approval gate. Never recompute a diff after approval.

**Deploy routing.** `deployToEnvironment` picks a strategy for each environment returned by `configEnvironmentsFor(cfg, branch)`: `deployGitops` (commits the image tag with `manifestBumpImage`, then Argo syncs; Jenkins never touches the cluster), `deployEcs`, `deployTerraform`, or `deployCloudFormation`. Environments deploy sequentially. For gitops the approval gate comes before the manifest commit. For infra it sits between plan and apply.

**Logic goes in Python, not Groovy.** Parsing logic (coverage, JSON, plan and change-set summaries, Sonar gate) lives in `resources/com/platformpipelines/scripts/*.py`. Steps call these scripts with `sh "python3 ${useScript('x.py')} ..."`. Groovy should only run commands and check exit codes.

**Shell safety.** Any config value that reaches a shell string must go through `shellQuote` or be validated first. The `*InjectionTest` classes lock this in.

## Tests

- Tests use JenkinsPipelineUnit. `test/groovy/BaseTest.groovy` registers every vars file as a callable step, so cross-step calls resolve. It stubs Jenkins steps and records every `sh` command in `shellCommands`.
- Helpers: `step('name').call(...)`, `configFile(yaml)` for `.ci/config.yaml`, `existingFiles` as the fake workspace, and `ranMatching(regex)`. Override `stubStdout(script)` / `stubStatus(script)` to control command output.
- If a step calls another step with an argument signature that isn't listed in `registerLibrarySteps()`, add that arity there.
- Tests are JUnit 4 with quoted-string method names and AssertJ assertions.

Guard tests that commonly fail after a change:
- `DocsCoverageTest`: every `vars/<name>.groovy` needs `docs/<section>/<name>.md` (section is `ci-cd`, `cloud`, or `other`) and an entry in the `mkdocs.yml` nav. Removing a step means removing its doc page too.
- `ExamplesConfigTest`: every `examples/*/.ci/config.yaml` must load cleanly, and every example Jenkinsfile must call a real entry point. Keep the examples in sync when you change config keys.
- `ManifestBumpImageTest`: covers kustomize, plain manifests, Helm values, registries with ports, and no-ops. A regex that silently fails to match leaves the old image deployed while the build goes green.
- Renaming a config key requires an old → new mapping in `configDeprecatedKeys`.
