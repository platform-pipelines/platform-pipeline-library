# platform-pipeline

Jenkins shared library for GitHub → lint → build → test → scan → GHCR +
GitHub releases → GitOps → Argo CD. Images go to GHCR and build artifacts to
GitHub releases, so there is no artifact store to run.

**Docs:** https://platform-pipelines.github.io/platform-pipeline-library/

## Quick start

A consuming repo's Jenkinsfile is two lines:

```groovy
@Library('platform-pipeline@main') _
standardPipeline()
```

Everything else is declared in `.ci/config.yaml`:

```yaml
appName: orders-api
buildTool: python          # maven | gradle | npm | python | go | docker-only | terraform | cloudformation
imageRepo: ghcr.io/acme/orders-api
```

## Examples

Start from the example closest to your repo. Each one is a complete repo root
that builds as-is.

| Example | Entry points | Shape |
|---|---|---|
| [`examples/go-service`](examples/go-service) | `standardPipeline`, `cdPipeline` | Go app, kaniko-docker, three gitops environments, CD promotion to prod |
| [`examples/java-service`](examples/java-service) | `standardPipeline` | Gradle app (Checkstyle, SpotBugs, JaCoCo), kaniko-k8s, GitHub release publish |
| [`examples/node-service`](examples/node-service) | `standardPipeline` | npm app, ESLint + Prettier with autofix, node:test coverage |
| [`examples/python-service`](examples/python-service) | `standardPipeline` | Flask app in a venv, ruff + mypy + pytest, buildah |
| [`examples/cd-promotion`](examples/cd-promotion) | `cdPipeline` | **CD**: promote dev → staging → prod or roll back, no rebuild |
| [`examples/aws-ecs-service`](examples/aws-ecs-service) | `standardPipeline`, `cdPipeline` | **AWS end to end**: ECR push via STS, ECS Fargate rollout with rollback, cross-account prod |
| [`examples/terraform-aws-platform`](examples/terraform-aws-platform) | `standardPipeline`, `terraformDriftPipeline` | **Terraform end to end**: state bootstrap, VPC/ALB/ECS/ECR, mocked tests, conftest per plan, approvals, nightly drift |
| [`examples/terraform-stack`](examples/terraform-stack) | `standardPipeline` | Terraform VPC: plan → approve → apply |
| [`examples/cloudformation-stack`](examples/cloudformation-stack) | `standardPipeline` | CloudFormation: change set → approve → execute |

## Documentation

The docs live in [`docs/`](docs/) and are published to the site above.

| Section | Contents |
|---|---|
| [Getting started](docs/getting-started/index.md) | Quickstart, examples, how to read a step page |
| [Guides](docs/guides/pipelines.md) | [Pipelines](docs/guides/pipelines.md) · [Configuration](docs/guides/configuration.md) · [Deployments](docs/guides/deployments.md) · [Infrastructure as code](docs/guides/infrastructure-as-code.md) · [Images & supply chain](docs/guides/images-and-supply-chain.md) · [Credentials](docs/guides/credentials.md) |
| [Step reference](docs/reference/index.md) | One page per step in `vars/`, grouped by purpose |
| [Contributing](docs/contributing/index.md) | Design rules, [extending the library](docs/contributing/extending.md), [local development](docs/contributing/local-development.md) |

## Contributing

```bash
make check        # tests + CodeNarc, exactly what CI runs (needs JDK 17)
make docs-serve   # preview the docs site locally
```

See [Contributing](docs/contributing/index.md) before adding a step, a config
key, or a language.
