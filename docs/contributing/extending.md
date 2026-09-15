# Extending the library

## Adding a step

1. Add `vars/<name>.groovy` with one `call()` and a header comment
   (`// Usage:`, `// Params:`, `// Returns:`).
2. Add `docs/reference/<section>/<name>.md`. `<section>` is one of `pipelines`,
   `languages`, `images`, `quality`, `deploy`, `infrastructure`, `config`,
   `github-slack`, `utilities`. Follow the
   [standard page layout](../getting-started/reading-the-reference.md#page-layout).
3. Add the page to the `mkdocs.yml` nav under its section.

`DocsCoverageTest` fails the build if any of the three is missing.

## Adding or renaming a config key

- **New key:** add it to [`configDefaults`](../reference/config/configDefaults.md)
  (or [`configEnvDefaults`](../reference/config/configEnvDefaults.md)), even if
  its default is `null`. That map is the schema: keys not listed there are
  reported as unknown.
- **Renamed key:** add the old → new mapping to
  [`configDeprecatedKeys`](../reference/config/configDeprecatedKeys.md), so
  existing repos keep working and are told where the key moved.
- Keep `examples/*/.ci/config.yaml` in sync. `ExamplesConfigTest` loads every one.

## Adding a language

Language steps share nothing except the dispatchers, so changing Go's lint
rules cannot affect Node.

| | lint | build | test | package |
|---|---|---|---|---|
| Go | `goLint` | `goBuild` | `goTest` | `goPackage` |
| Python | `pythonLint` | `pythonBuild` | `pythonTest` | `pythonPackage` |
| Java (Maven) | `mavenLint` | `mavenBuild` | `mavenTest` | `mavenPackage` |
| Java (Gradle) | `gradleLint` | `gradleBuild` | `gradleTest` | `gradlePackage` |
| Node | `nodeLint` | `nodeBuild` | `nodeTest` | `nodePackage` |
| docker-only | `dockerOnlyLint` | — | — | — |
| Terraform | `terraformLint` | `terraformBuild` | `terraformTest` | `terraformPackage` |
| CloudFormation | `cfnLint` | `cfnBuild` | `cfnTest` | `cfnPackage` |

To add Rust, for example:

1. Write `rustLint`, `rustBuild`, `rustTest`, `rustPackage`.
2. Add a case to each of the four dispatchers: `lintApp`, `buildApp`, `testApp`, `packageApp`.
3. Add a case to each `app*` metadata step: `appToolImage`, `appTestReport`,
   `appCoverageFile`, `appArtifacts`, `appCacheDir`, `appLintReport`, `appSonarProps`.
4. Add `rust` to `configSupportedTools`.

`AppMetadataTest` walks `configSupportedTools`, so a missing metadata case
fails the build rather than silently producing a stage that does nothing.
Dispatchers fail loudly on an unknown value rather than skipping.

## Next

[Local development](local-development.md)
