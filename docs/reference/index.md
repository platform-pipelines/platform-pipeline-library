# Step reference

One page per callable step in
[`vars/`](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/vars),
grouped by what the step does. New here? Read
[Reading the step reference](../getting-started/reading-the-reference.md) first.

| Section | Use it for | Start with |
|---|---|---|
| [Pipelines](pipelines/index.md) | Entry points, stage orchestration, `buildTool` dispatch and metadata | [standardPipeline](pipelines/standardPipeline.md) |
| [Languages](languages/index.md) | Per-language lint / build / test / package | [buildApp](pipelines/buildApp.md) |
| [Images](images/index.md) | Container builds, registries, where steps run, SBOM and signing | [buildImage](images/buildImage.md) |
| [Quality & scanning](quality/index.md) | Sonar, Trivy, secrets, dependencies, coverage | [qualityChecks](quality/qualityChecks.md) |
| [Deploy](deploy/index.md) | Strategy routing, GitOps, ECS, Argo CD, approvals, releases | [deployToEnvironment](deploy/deployToEnvironment.md) |
| [Infrastructure](infrastructure/index.md) | AWS credentials, Terraform, CloudFormation | [deployTerraform](infrastructure/deployTerraform.md) |
| [Configuration](config/index.md) | Loading, merging and validating `.ci/config.yaml` | [configLoad](config/configLoad.md) |
| [GitHub & Slack](github-slack/index.md) | GitHub REST API, PR comments, releases, Slack notifications | [githubApiRequest](github-slack/githubApiRequest.md) |
| [Utilities](utilities/index.md) | Logging, versioning, shell quoting, bundled scripts | [logInfo](utilities/logInfo.md) |
