# terraform-aws-platform example

End-to-end Terraform on AWS: the VPC, HTTPS load balancer, ECS Fargate
cluster and service, and ECR repositories that
[aws-ecs-service](../aws-ecs-service) deploys into — across three environments
and two AWS accounts, with state bootstrap, mocked tests, policy checks on
every plan, approvals, and nightly drift detection.

```
Jenkinsfile (standardPipeline, branch main)

Lint ─ fmt -check · validate · tflint
Build ─ init -backend=false · validate
Test ─ terraform test (mock provider, no credentials)
Quality ─ trivy config · gitleaks
Deploy: dev ──── assume JenkinsDeploy@1111 → init (backends/dev.hcl) → plan → conftest → apply
Deploy: staging ─ assume JenkinsDeploy@1111 → init → plan → conftest → PR comment → approve → apply the saved plan
Deploy: prod ─── assume JenkinsDeploy@4444 → init → plan → conftest → PR comment → approve → apply the saved plan

Jenkinsfile.drift (terraformDriftPipeline, weekdays 06:xx)
Plan dev · staging · prod against real state → UNSTABLE + Slack when anything differs; never applies
```

## Layout

```
.ci/config.yaml          environments, accounts, roles, approvals
Jenkinsfile              standardPipeline()
Jenkinsfile.drift        terraformDriftPipeline(schedule: 'H 6 * * 1-5')
bootstrap/               one-off per account: state bucket + JenkinsDeploy role
terraform/
  versions.tf providers.tf variables.tf outputs.tf
  network.tf             VPC, public/private subnets, NAT, flow logs
  alb.tf                 HTTPS ALB, HTTP→HTTPS redirect
  ecs.tf                 cluster, roles, first task definition, service (circuit breaker)
  ecr.tf                 app + kaniko cache repositories, cross-account pull
  common.tfvars env/     shared and per-environment variables
  backends/              per-environment S3 backend (S3-native locking)
  tests/                 terraform test, mock provider
  policies/              conftest rules run against every plan
  .tflint.hcl
```

## Set-up, once

1. **Bootstrap each account** (admin credentials, locally):

   ```bash
   cd bootstrap
   terraform init
   terraform apply -var account_id=111122223333 -var 'jenkins_principal_arns=["arn:aws:iam::111122223333:user/jenkins"]'
   ```

   Repeat with `account_id=444455556666` for prod. This creates
   `acme-tfstate-<account>-eu-west-1` and the `JenkinsDeploy` role.
2. **Jenkins credential**: an `aws-credentials` username/password credential
   (access key ID / secret) for the principal you trusted above. Every plan
   and apply assumes the environment's role from it; the session name carries
   the build number and commit, so CloudTrail shows which build made each call.
3. **ACM certificates**: put a certificate ARN per environment into
   `terraform/env/*.tfvars`.
4. **Jobs**: a Multibranch Pipeline on `Jenkinsfile`, and a Pipeline job on
   `Jenkinsfile.drift` (branch `main`).
5. **First deploy of the app**: the service starts on `bootstrap_image`
   (unprivileged nginx on 8080), which fails the `/healthz` check until the
   first [aws-ecs-service](../aws-ecs-service) build replaces it.

## Guarantees worth knowing

- **The applied plan is the approved plan.** `terraformApply` consumes the
  saved `tfplan-<env>` file; it never re-plans after approval.
- **Policy runs before approval.** `conftest` checks each environment's plan
  JSON; a plan that breaks `policies/` fails before anyone is asked to approve it.
- **Wrong-account applies fail.** `allowed_account_ids` in the provider
  rejects a prod var file with dev credentials at plan time.
- **Pipeline and Terraform do not fight over the service.** The ECS service
  ignores `task_definition` and `desired_count`, which the app pipeline and
  autoscaling own.
- **Tests need no AWS.** `tests/platform.tftest.hcl` uses `mock_provider`, so
  it runs on every branch with no credentials.

## Validate locally

```bash
cd terraform
terraform fmt -check -recursive
terraform init -backend=false
terraform validate
terraform test
tflint --init && tflint
trivy config --severity HIGH,CRITICAL --exit-code 1 .
```

## Cost note

Each environment runs one NAT gateway and one ALB, which bill hourly even when
idle. Set `enable_nat_gateway = false` for a sandbox that does not need
outbound internet from private subnets (add VPC endpoints for ECR, S3 and
CloudWatch Logs instead).
