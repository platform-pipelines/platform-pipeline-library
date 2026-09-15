# cloudformation-stack example

An infrastructure repo built with CloudFormation. Setting
`buildTool: cloudformation` switches the pipeline into its infra shape: no
image, no SBOM, and deploy becomes **change set → approve → execute**, where
the change set that gets executed is the one the approver saw.

## Using this in a real repo

1. Copy `Jenkinsfile` to the root of your repo.
2. Keep `.ci/config.yaml` (already in place), then set `appName`, the template
   paths, `infra.artifactBucket` (or remove it if templates have no local
   artifacts), stack names, parameters and role ARNs.
3. Make sure the `aws-credentials` credential (and `github-token` for PR
   comments) exists on the controller — see the root README's Credentials
   table.

## What each stage runs

| Stage | Step | What happens |
|---|---|---|
| Lint | `cfnLint` | `cfn-lint` over every template |
| Build | `cfnBuild` | `aws cloudformation validate-template` for each template |
| Test | `cfnTest` | `checkov` policy checks; its JUnit report is published as test results |
| Package | `cfnPackage` | `aws cloudformation package` to `infra.artifactBucket` (skipped when unset) |
| Quality | `scanIac` | `trivy config` over `infra.workingDir` |
| Deploy | `deployCloudFormation` | per environment: change set → PR summary → approval (if required) → execute |

## What each section demonstrates

- **infra** — template location, artifact bucket, region (required) and the
  IAM capabilities the stack needs.
- **environments** — each one names its stack and passes its own parameters.
  `prod` assumes a dedicated deploy role and requires approval.
- **quality** — Sonar off, Trivy config scan and secret scan on.
- **notify** — `on: always`, because every infra change is worth announcing.
