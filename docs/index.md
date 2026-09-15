# platform-pipeline

A Jenkins shared library for GitHub → lint → build → test → scan → GHCR +
GitHub releases → GitOps → Argo CD, driven by one `.ci/config.yaml` per
consuming repo.

```groovy
@Library('platform-pipeline@main') _
standardPipeline()
```

## Where to go

| If you want to… | Read |
|---|---|
| Set up a repo in five minutes | [Quickstart](getting-started/index.md), then copy an [example](getting-started/examples.md) |
| Understand how a run works | [Guides](guides/pipelines.md): pipelines, configuration, deployments, infrastructure as code, images, credentials |
| Look up a specific step | [Step reference](reference/index.md), grouped by what the step does |
| Change the library itself | [Contributing](contributing/index.md): design rules, adding steps and languages, local development |
