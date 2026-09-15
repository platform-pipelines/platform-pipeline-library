# Configuration

Every repo has one `.ci/config.yaml`. Only `appName`, `buildTool`, and
`imageRepo` (when `containerize: true`) are required. Everything else defaults
from [`configDefaults`](../reference/config/configDefaults.md). Environment
entries default from [`configEnvDefaults`](../reference/config/configEnvDefaults.md).

## Keys

Defaults are shown in parentheses.

| Section | Keys |
|---|---|
| top level | `appName`, `buildTool`, `runtimeVersion`, `imageRepo`, `containerize` (true), `dockerfile` (`Dockerfile`), `imageBuilder` (`kaniko-docker`), `gitopsRepo`, `gitopsBranch` (`main`), `deployStrategy` (derived) |
| `lint` | `enabled` (true), `failOnError` (true), `autoFormat` (false) |
| `quality` | `sonar` (true), `sonarProjectKey` (appName), `sonarSources` (`.`), `sonarExclusions`, `failOnQualityGate` (true), `trivy` (true), `trivyFailOn` (HIGH, CRITICAL), `trivyIgnoreUnfixed` (true), `secretScan` (true), `dependencyCheck` (false), `dependencyCheckCvss` (7), `minCoverage`, `sbom` (true), `signImage` (false) |
| `publish` | `githubRelease` (false): attach build artifacts to the `v<version>` GitHub release; `branchPattern` (`main`) |
| `approval` | `allowSelfApproval` (false) |
| `notify` | `slackChannel`, `on` (`change`, or `always` / `failure`), `githubChecks` (true) |
| `infra` | `workingDir` (`.`), `varFiles`, `backendConfig`, `policyDir`, `region` (`us-east-1`), `assumeRole`, `awsCredentialsId` (`aws-credentials`), `template`, `templates`, `artifactBucket`, `capabilities` |
| `environments[]` | `name`, `namespace` (name), `manifestPath`, `branchPattern` (`main`), `requiresApproval`, `approvers`, `approvalTimeoutMinutes` (60), `promoteFrom`, `ecsCluster`, `ecsService`, `ecsContainer` (appName), `workspace`, `backendConfig`, `varFiles`, `stackName`, `parameters`, `region`, `assumeRole`, `awsCredentialsId` |
| `extra` | free-form; never read or checked by the pipeline. Use it for your own tooling. |

Each set of allowed values lives in one place:
[`configSupportedTools`](../reference/config/configSupportedTools.md) (`buildTool`),
[`configImageBuilders`](../reference/config/configImageBuilders.md),
[`configDeployStrategies`](../reference/config/configDeployStrategies.md).

## Validation

**Typos are reported.** A key the library does not recognise is logged at
Init, with a hint:

```
[WARN]  unknown config key 'quality.minCoverge' — did you mean 'minCoverage'? (in .ci/config.yaml)
```

Unknown keys warn. Invalid values (an unsupported `buildTool`, a missing
`imageRepo`, ...) fail the build, and every problem is listed at once.

## How config is loaded

[`initPipeline`](../reference/pipelines/initPipeline.md) →
[`configLoad`](../reference/config/configLoad.md) reads `.ci/config.yaml`,
applies [`configDeprecatedKeys`](../reference/config/configDeprecatedKeys.md)
migrations, merges it over the defaults with
[`configMerge`](../reference/config/configMerge.md), then runs
[`configUnknownKeys`](../reference/config/configUnknownKeys.md) and
[`configValidate`](../reference/config/configValidate.md). The result is a plain
`Map` that every other step reads.

## Migrating from `extra.*`

These keys used to be read out of `extra`. They still work, with a deprecation
warning, until you move them:

| Old | New |
|---|---|
| `extra.sonarSources` | `quality.sonarSources` |
| `extra.sonarExclusions` | `quality.sonarExclusions` |
| `extra.dependencyCheckCvss` | `quality.dependencyCheckCvss` |
| `extra.allowSelfApproval` | `approval.allowSelfApproval` |

If both are set, the new key wins. The mapping lives in
[`configDeprecatedKeys`](../reference/config/configDeprecatedKeys.md).

## Next

[Deployments](deployments.md)
