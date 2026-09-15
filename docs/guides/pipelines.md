# Pipelines

A consuming repo calls one entry point. Everything else comes from
`.ci/config.yaml`.

## Entry points

| Entry point | Job type | What it does |
|---|---|---|
| [`standardPipeline()`](../reference/pipelines/standardPipeline.md) | Multibranch | CI and deploy: lint → build → test → scan → package/image → deploy per environment |
| [`cdPipeline()`](../reference/pipelines/cdPipeline.md) | Pipeline (`ENVIRONMENT`, `IMAGE_TAG`, `DRY_RUN`) | CD only: promote the tag the `promoteFrom` environment runs, or deploy/roll back a given tag; checks the tag exists first |
| [`terraformDriftPipeline(schedule: '…')`](../reference/pipelines/terraformDriftPipeline.md) | Pipeline (cron) | Plans every Terraform environment against real state; UNSTABLE + Slack on drift; never applies |

## Stages

```
Init → Lint → Build → Test → Quality & Security → Package → Scan Image → Deploy
                                    (parallel)       │                    (per env)
                                                     └─ build · SBOM · sign
```

| Stage | Driven by |
|---|---|
| Init | [initPipeline](../reference/pipelines/initPipeline.md) loads and validates config and prints what the build will do |
| Lint / Build / Test / Package | [lintApp](../reference/pipelines/lintApp.md), [buildApp](../reference/pipelines/buildApp.md), [testApp](../reference/pipelines/testApp.md), [packageApp](../reference/pipelines/packageApp.md) dispatch on `buildTool` to the [language steps](../reference/languages/index.md) |
| Quality & Security | [qualityChecks](../reference/quality/qualityChecks.md) runs Sonar, Trivy, secret scan and dependency check in parallel |
| Package / Scan Image | [buildImage](../reference/images/buildImage.md), [generateSbom](../reference/images/generateSbom.md), [signImage](../reference/images/signImage.md), [scanTrivy](../reference/quality/scanTrivy.md) |
| Deploy | [deployToEnvironment](../reference/deploy/deployToEnvironment.md) for each environment this branch reaches; see [Deployments](deployments.md) |

## What each lint step runs

Each language gets four explicit steps plus its metadata. Nothing is shared
except the dispatcher, so changing Go's lint rules cannot affect Node.

- **Go**: `gofmt -l`, `go vet`, `golangci-lint`
- **Python**: `ruff check`, `ruff format --check`, `mypy` when configured
- **Java**: Checkstyle and SpotBugs
- **Node**: ESLint, Prettier, and `tsc --noEmit` when `tsconfig.json` exists
- **docker-only**: hadolint against the Dockerfile
- **Terraform**: `fmt -check`, `validate`, tflint
- **CloudFormation**: cfn-lint, then `aws cloudformation validate-template`

`lint.failOnError: false` makes any of them report-only. The Node typecheck is
the exception: type errors always fail, because shipping a TypeScript build
that does not typecheck is not a style preference.

## Agents and the toolbox image

Set `CI_TOOLBOX_IMAGE` on the controller to the published
[toolbox](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/toolbox)
image. Build steps then run inside it on any docker agent. Without it they fall
back to stock language images, which lack golangci-lint, ruff, tflint, conftest,
cfn-lint, checkov and python3. Agents that *are* the toolbox set
`CI_TOOLBOX=true` and run steps in place.

## Next

[Configuration](configuration.md)
