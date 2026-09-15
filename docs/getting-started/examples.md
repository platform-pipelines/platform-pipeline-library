# Examples

Start from the example closest to your repo. Each one is a complete repo root
(`.ci/config.yaml`, Jenkinsfile(s), source, tests, Dockerfile or IaC) that
passes its own lint, tests and coverage floor. Copying the directory gives you
a buildable repo.

| Example | Entry points | Shape |
|---|---|---|
| [go-service](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/go-service) | `standardPipeline`, `cdPipeline` | Go app, kaniko-docker, three gitops environments, CD promotion to prod |
| [java-service](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/java-service) | `standardPipeline` | Gradle app (Checkstyle, SpotBugs, JaCoCo), kaniko-k8s, GitHub release publish |
| [node-service](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/node-service) | `standardPipeline` | npm app, ESLint + Prettier with autofix, node:test coverage |
| [python-service](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/python-service) | `standardPipeline` | Flask app in a venv, ruff + mypy + pytest, buildah |
| [cd-promotion](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/cd-promotion) | `cdPipeline` | **CD**: promote dev → staging → prod or roll back, no rebuild |
| [aws-ecs-service](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/aws-ecs-service) | `standardPipeline`, `cdPipeline` | **AWS end to end**: ECR push via STS, ECS Fargate rollout with rollback, cross-account prod |
| [terraform-aws-platform](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/terraform-aws-platform) | `standardPipeline`, `terraformDriftPipeline` | **Terraform end to end**: state bootstrap, VPC/ALB/ECS/ECR, mocked tests, conftest per plan, approvals, nightly drift |
| [terraform-stack](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/terraform-stack) | `standardPipeline` | Terraform VPC: plan → approve → apply |
| [cloudformation-stack](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/cloudformation-stack) | `standardPipeline` | CloudFormation: change set → approve → execute |

The examples cannot drift from what the library accepts: `ExamplesConfigTest`
loads every example config and checks that every Jenkinsfile calls a real
entry point.

## Next

[Reading the step reference](reading-the-reference.md)
