# ci-toolbox

One image with every toolchain and scanner `company-pipeline` uses.

## Build

```bash
docker build -t ghcr.io/acme/ci-toolbox:1.0.0 toolbox/
docker run --rm ghcr.io/acme/ci-toolbox:1.0.0 bash < toolbox/verify.sh
docker push ghcr.io/acme/ci-toolbox:1.0.0
```

Never push an image that has not passed `verify.sh`. A missing tool should
fail there, not three stages into somebody's pipeline.

## What's in it

| | version |
|---|---|
| Java (Temurin JDK) | 21 |
| Maven | 3.9.16 |
| Gradle | 9.8.0 |
| Node | 24 (Active LTS) |
| Go | 1.27.0 |
| Python | 3.12 + ruff, mypy, pytest, build |
| golangci-lint | 2.13.1 |
| Trivy | 0.74.0 |
| Gitleaks | 8.30.1 |
| hadolint | 2.15.1 |
| shellcheck | 0.11.0 |
| SonarScanner CLI | 6.2.1.4610 |
| Argo CD CLI | 3.5.1 |
| yq / jq | 4.53.6 / distro |

## Using it

Mark toolbox agents with `CI_TOOLBOX=true`. `inBuildContainer` and
`inToolContainer` check it and run steps directly instead of wrapping them in
a per-language container.

In JCasC:

```yaml
jenkins:
  nodes:
    - permanent:
        name: toolbox-agent-1
        labelString: "linux docker toolbox"
        nodeProperties:
          - envVars:
              env:
                - key: CI_TOOLBOX
                  value: "true"
```

Without that variable the library falls back to per-language images, so a
mixed fleet works during migration.

## What's deliberately not in it

**Kaniko.** It expects to be the container (`/kaniko/executor` against its own
filesystem) rather than a binary you invoke. Image builds still run in a
kaniko sidecar. That is also what keeps agents free of a docker socket.

**OWASP Dependency-Check.** Its NVD database is several GB and changes daily,
so baking it in would make the image enormous and instantly stale. It stays a
separate image with a cached volume, which is also why it is opt-in per repo.

## Upgrading

Version numbers are `ARG`s at the top of the Dockerfile, so a bump is a
one-line PR. The sequence:

1. Bump the ARG.
2. Build and run `verify.sh`.
3. Run the library's own test suite against the new image.
4. Tag and push, then move one canary repo before the fleet.

Two pairs move together and will break if separated:

- **Go and golangci-lint** — golangci-lint only supports Go versions it was
  built against. 2.13 added Go 1.27 support.
- **golangci-lint major and `goLint.groovy`** — v2 removed `--out-format` in
  favour of `--output.checkstyle.path`. `verify.sh` asserts the flag exists so
  a v3 that renames it again fails at image build, not at pipeline run.

## Size

Roughly 2.5GB. A Go-only repo still pulls the JDK, which is the honest cost of
one image. If pull time becomes the bottleneck, split into `toolbox-jvm` and
`toolbox-scripting` and add a `toolboxImage:` key to `.ci/config.yaml`.
