# Example: containerized Python service

A full end-to-end config for a Python web service (`orders-api`). It turns on
every major capability the library offers for an application repo. A
background worker with no image would instead set `containerize: false` and
drop `imageRepo`, `gitopsRepo` and `environments`.

## What this demonstrates

- **Containerized build** — `imageRepo` + `dockerfile` + `imageBuilder:
  buildah` (the toolbox-agent path, no docker socket needed).
- **Full quality/security gate** — Sonar with a quality gate, Trivy on both
  dependencies and image, secret scanning, a pip dependency-CVE pass, a
  coverage floor, an SBOM, and cosign image signing.
- **Multi-environment GitOps deploy** — dev (every branch), staging (`main`),
  and prod (`main`, gated behind approval with named approvers and a timeout).
- **Artifact publishing** — `publish.nexusRepo` uploads the built wheel/sdist.
- **Notifications** — Slack on every result change, plus GitHub check runs.

## Adapting this to a real repo

1. Copy `Jenkinsfile` to the repo root.
2. Keep `.ci/config.yaml` (already in place).
3. Change `appName`, `imageRepo`, `gitopsRepo`, the `namespace`/`manifestPath`
   values, and the Slack channel to match the real service.
4. Confirm the GitOps repo already has the three `kustomization.yaml` paths
   referenced here, or point `manifestPath` at ones that exist.

## Section-by-section

| Section | Purpose |
|---|---|
| `imageRepo` / `dockerfile` / `imageBuilder` | how and where the container image is built |
| `gitopsRepo` / `gitopsBranch` | where a passing build commits the new image tag |
| `lint` | ruff + mypy; `failOnError: true` means lint problems fail the build |
| `environments` | one deploy target per Argo CD app, gated by branch and (for prod) approval |
| `quality` | Sonar (incl. exclusions), Trivy, secret scan, dependency scan, coverage floor, SBOM, signing |
| `publish` | where build artifacts are uploaded |
| `approval` | whether the person who triggered a build may approve its deploy |
| `notify` | Slack channel/trigger and GitHub status checks |
