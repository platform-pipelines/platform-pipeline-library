# go-service example

A full end-to-end example of this library's capabilities applied to a Go
application repo: full quality gate, image signing, SBOM, and a
three-environment gitops promotion path with an approval gate in front of
prod. A minimal repo needs only `appName`, `buildTool` and `imageRepo` (or
`imageRegistry: github`) — everything else here is a default made explicit or
an opt-in.

## Using this in a real repo

1. Copy `Jenkinsfile` to the root of your repo.
2. Keep `.ci/config.yaml` (already in place) in your repo, then edit the
   values (`appName`, `gitopsRepo`, environment names/paths, Slack channel,
   approvers) to match your service. The image path is derived from your repo.
3. Make sure the credentials this library expects
   (`github-token`, `sonar-token`, `slack-webhook`, `ghcr-credentials` with a
   classic PAT that has `write:packages`) are configured on the controller —
   see the root README's Credentials table.

## What each section demonstrates

- **Container image** — `imageRegistry: github` pushes to
  `ghcr.io/<owner>/<repo>`, the GitHub repo the code lives in, with
  `kaniko-docker`; swap to `kaniko-k8s` or `buildah` per the trade-off table
  in the root README.
- **publish** — `githubPackages: true` pushes the compiled binaries in
  `dist/` to `ghcr.io/<owner>/<repo>/edge-router-artifacts:<tag>`, on the same
  repo's Packages tab as the image.
- **lint** — `failOnError: true`, so `gofmt`/`go vet`/`golangci-lint`
  failures block the build rather than just reporting.
- **environments** — `dev` (every branch), `staging` (main), `prod` (main,
  gated). Deploys run in that order, sequentially, so a broken dev build
  never reaches staging.
- **quality** — Sonar, Trivy (with an ignore-unfixed carve-out), secret
  scanning, dependency checking, a coverage floor, SBOM generation, and
  cosign image signing all turned on.
- **notify** — Slack on every state change, plus GitHub status checks.
- **extra** — a free-form field the pipeline never reads or validates; use it
  for your own team's tooling.

See the root [README.md](../../README.md) for the full stage list, the
credentials table, and how the CloudFormation/Terraform infra shape differs
from this application shape.
