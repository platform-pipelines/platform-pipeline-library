# aws-ecs-service example

End to end on AWS: a Go service built, scanned and pushed to **Amazon ECR**,
then rolled out to **ECS Fargate** in dev and staging by CI, and promoted to a
separate prod account by a CD job with an approval gate.

```
Jenkinsfile (standardPipeline)                                        Jenkinsfile.cd (cdPipeline)
Init → Lint → Build → Test → Quality → Package ─────→ Scan Image → Deploy: dev → Deploy: staging     ENVIRONMENT=prod → approve → Deploy: prod
                                        │  push to ECR (token from STS)        │                         │
                                        └  SBOM                                 └─ register task def      └─ promote the tag staging runs
                                                                                   update service
                                                                                   wait services-stable
                                                                                   roll back if it never stabilises
```

The infrastructure — VPC, ALB, ECS cluster and service, ECR repositories,
IAM roles — is [terraform-aws-platform](../terraform-aws-platform). Apply that
first; this repo only ever changes the image the service runs.

## Using this in a real repo

1. Copy this directory to a new repo root (`.ci/config.yaml`, both
   Jenkinsfiles, the Go source).
2. Apply [terraform-aws-platform](../terraform-aws-platform) for `dev`,
   `staging` and `prod`. Put its outputs into `.ci/config.yaml`: the ECR
   repository URL as `imageRepo`, cluster and service names per environment.
3. Create the IAM roles named in the config (`JenkinsEcrPush`, and
   `JenkinsDeploy` in each account — the platform bootstrap creates them)
   and an `aws-credentials` credential in Jenkins allowed to assume them.
4. Create two jobs: a **Multibranch Pipeline** on `Jenkinsfile`, and a
   **Pipeline** job on `Jenkinsfile.cd` (branch `main`).

## What each stage runs

| Stage | Step | On AWS |
|---|---|---|
| Lint / Build / Test | `goLint`, `goBuild`, `goTest` | — (coverage floor 75%) |
| Quality | `scanTrivy` (fs), `scanSecrets` | — |
| Package | `goPackage`, then `withRegistryAuth` → `buildImage`, `generateSbom` | `sts:AssumeRole` JenkinsEcrPush → `ecr:GetAuthorizationToken` → kaniko pushes `:<version>`, `:<sha>`, `:latest` and the layer cache to `payments-api/cache` |
| Scan Image | `scanTrivy` (image) | pulls the pushed image from ECR |
| Deploy: dev, staging | `deployEcs` | assume JenkinsDeploy → describe service → register a task definition revision with only the image changed → `update-service` → `wait services-stable` → on timeout, restore the previous revision and fail |
| CD: prod | `cdResolveImage`, `imageDigest`, `approvalGate`, `deployEcs` | reads the image staging runs from its ECS service, confirms the tag in ECR, waits for approval, then the same rollout in the prod account |

## Why the pipeline copies the running task definition

Terraform owns the task definition's shape (CPU, memory, roles, logging,
secrets) and ignores later changes to the service's `task_definition`. The
pipeline takes the revision the service runs now and changes only the
container image. Neither tool overwrites the other, and a Terraform change to
memory is picked up by the next deploy instead of being reverted by it.

## Rollback

- **Automatic** — the service has the ECS deployment circuit breaker with
  rollback, and `deployEcs` also restores the previous revision if the service
  is not stable within 10 minutes.
- **Manual** — run the CD job with `ENVIRONMENT=prod IMAGE_TAG=<previous tag>`.

## Credentials

| Jenkins credential | Used for |
|---|---|
| `aws-credentials` | base identity that assumes `JenkinsEcrPush` and each environment's `JenkinsDeploy` |
| `github-token` | commit statuses |
| `slack-webhook` | `#payments-deploys` |

No registry credential: the ECR token comes from STS on every build.
