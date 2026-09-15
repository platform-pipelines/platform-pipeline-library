# Credentials & environment

## Jenkins credentials

| ID | Kind | Used by |
|---|---|---|
| `github-token` | string | statuses, PR comments, GitOps commits, release artifacts (`publish.githubRelease`; needs `contents: write`) |
| `github-scm` | username/password (same token) | git checkout, multibranch / GitHub Branch Source jobs (local stack) |
| `sonar-token` | string | scan and quality gate |
| `slack-webhook` | string | notifications |
| `argocd-token` | string | sync wait |
| `ghcr-credentials` | username/password | registry push (override with `REGISTRY_CREDENTIALS_ID`) |
| `aws-credentials` | username/password (access key id / secret) | Terraform and CloudFormation; override per repo or environment with `awsCredentialsId` |
| `cosign-oidc-token` | string | keyless image signing (`quality.signImage`) |

## Controller environment variables

Set these on the controller through JCasC.

| Variable | Required | Purpose |
|---|---|---|
| `SONAR_HOST_URL` | yes | SonarQube server |
| `ARGOCD_SERVER` | yes | Argo CD server for sync waits |
| `CI_TOOLBOX_IMAGE` | recommended | image build steps run in; see [Pipelines](pipelines.md#agents-and-the-toolbox-image) |
| `PIPELINE_DEBUG` | no | `true` shows [logDebug](../reference/utilities/logDebug.md) output |
| `GITHUB_CREDENTIALS_ID` | no | overrides `github-token` |
| `REGISTRY_CREDENTIALS_ID` | no | overrides `ghcr-credentials` |
| `GITHUB_API_URL` | no | GitHub Enterprise API base URL |
