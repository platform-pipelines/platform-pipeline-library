# Local development

## Running the tests

```bash
make test    # Groovy / jenkins-pipeline-unit suite      (./gradlew test)
make lint    # CodeNarc over vars/ and test/             (./gradlew codenarcMain codenarcTest)
make check   # test + lint — exactly what CI runs
```

- **No Jenkins instance needed.** `BaseTest` registers every `vars` file as a
  callable step so cross-step calls resolve. It also records shell commands,
  so tests can assert on what would have run.
- **CodeNarc** uses the ruleset at `config/codenarc/codenarc.groovy` and allows
  zero priority-1/2 violations. `make check` fails the same way CI's
  `.github/workflows/ci.yml` does.
- **JDK 17 is required.** Groovy 3.0.19 cannot read newer class files. On macOS
  the Makefile finds a JDK 17 through `/usr/libexec/java_home -v 17`. On other
  platforms, set `JAVA_HOME` to a JDK 17 yourself.

The Python resource scripts are testable on their own:

```bash
python3 resources/com/platformpipelines/scripts/coverage_percent.py coverage.xml
```

`coverage_percent.py` handles cobertura, JaCoCo, Go coverprofile, and lcov, and
each format is verified against real report samples.

`manifestBumpImage` gets the most test attention. A pattern that fails to match
means the pipeline goes green while the old image stays deployed, so it is
covered against kustomize, plain manifests, Helm values, registries with ports,
inline list items, and the already-current no-op.

## CI toolbox

The pipeline shells out to Java, Node, Go, Python, Trivy, Gitleaks, hadolint,
and more. They are all bundled into one `ci-toolbox` image (see `toolbox/`), so
CI and your laptop run the same tool versions.

```bash
make toolbox-build    # build ci-toolbox:local
make toolbox-verify   # build, then run toolbox/verify.sh inside it
```

`toolbox-verify` smoke-tests that every tool the pipeline depends on exists at
the expected version inside the image. Run it after bumping any tool version in
`toolbox/Dockerfile`.

## Local Jenkins stack

```bash
make toolbox-build    # once: the image build steps run in (CI_TOOLBOX_IMAGE)
make local-up         # build and start Jenkins + agent + SonarQube in the background
make local-logs       # follow logs for all services
make local-down       # stop the stack, keep data volumes
make local-clean      # stop the stack and delete data volumes
make local-restart    # local-down + local-up
```

The stack runs Jenkins on :8080 (`admin` / `$JENKINS_ADMIN_PASSWORD`, default
`admin`) and SonarQube on :9000 (admin/admin). Jenkins builds from
`local/plugins.txt`: plugins are pinned, with no UI installs. The `agent`
service is the `linux-agent-1` node every stage runs on. It reaches Docker
through the host socket and runs build steps inside `ci-toolbox:local`.

**The library is your checkout, not GitHub.** Compose mounts this repo into
Jenkins as the `platform-pipeline` library, so you can test local commits
without pushing. Jenkins reads commits, not uncommitted edits: commit (no push
needed), then pick the branch in the test job:

```groovy
@Library('platform-pipeline@my-branch') _
standardPipeline()
```

Set `LIBRARY_REMOTE` to a git URL to load from a remote instead.

**Secrets go in `local/.env`.** The first `make local-up` copies
`local/.env.example` to `local/.env` (gitignored). Fill in `GITHUB_USER` and
`GITHUB_TOKEN`, plus any other [credential](../guides/credentials.md) you need,
and run `make local-up` again. Compose reads the file, and
`local/casc/jenkins.yaml` creates the Jenkins credentials at startup. The token
becomes both `github-token` (secret text, for the API steps) and `github-scm`
(username/password, for checkout and multibranch jobs). Shell exports override
the file. Editing `.env` or `jenkins.yaml` only needs `make local-up`, not
`local-clean`.

Artifacts and images need no local service. They go to GitHub releases and
GHCR using the `github-token` and `ghcr-credentials` credentials.

## Docs site

```bash
make docs-serve    # install mkdocs-material into .venv-docs with uv, serve with live reload
make docs-build    # build the static site into site/
```

CI builds with `mkdocs build --strict`, so a broken link fails the docs job.

## Versioning this library

Consuming repos currently point at `@Library('platform-pipeline@main')`.
Floating on `main` means every merge reaches every pipeline on its next build.
That is convenient while the library is young and changing fast, but one bad
merge breaks every pipeline at once.

Once the library and its consumers stabilise, switch to pinned release tags
(`@Library('platform-pipeline@v2')`). The release flow is: PR → unit tests →
merge → tag `v2.x` → smoke test one canary repo → move consumers to the new
tag → announce. Floating on `main` is a deliberate, temporary trade-off, not
the long-term plan.
