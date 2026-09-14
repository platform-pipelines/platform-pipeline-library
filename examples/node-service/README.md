# node-service example

A full end-to-end example of this library's capabilities applied to a
production Node.js API repo. Unlike `examples/config-node.yaml` (a front-end
repo still adopting Prettier, lint report-only, single environment), this is
what a mature service repo's config looks like once every quality gate is
actually enforced: build breaks on lint, on coverage, on a critical CVE, on a
leaked secret — and every image that reaches prod is signed.

## Using this in a real repo

1. Copy `Jenkinsfile` to the root of your repo.
2. Copy `config.yaml` to `.ci/config.yaml` in your repo, then edit the
   values (`appName`, `imageRepo`, `gitopsRepo`, environment names/paths,
   Slack channel, approvers) to match your service.
3. Make sure the credentials this library expects
   (`github-token`, `sonar-token`, `slack-webhook`, `ghcr-credentials`) are
   configured on the controller — see the root README's Credentials table.

## What each section demonstrates

- **Container image** — `containerize: true` with `kaniko-docker`, since
  these agents already have a docker socket from the default plugin set.
- **lint** — `failOnError: true` and `autoFormat: true`: ESLint/Prettier are
  fully adopted here, so `eslint --fix`/`prettier --write` run first and the
  build only fails on what autofix can't solve — a stricter bar than the
  report-only front-end example.
- **environments** — `dev` (every branch), `staging` (main), `prod` (main,
  gated). Deploys run in that order, sequentially, so a broken dev build
  never reaches staging.
- **quality** — Sonar, Trivy (with an ignore-unfixed carve-out), secret
  scanning, dependency checking, a coverage floor, SBOM generation, and
  cosign image signing all turned on.
- **notify** — Slack on every state change, plus GitHub status checks.
- **extra** — a free-form field the pipeline never reads; use it for your
  own team's tooling.

See the root [README.md](../../README.md) for the full stage list, the
credentials table, and how the CloudFormation/Terraform infra shape differs
from this application shape.
