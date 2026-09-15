# platform-pipeline

Jenkins shared library for GitHub → lint → build → test → scan → GHCR →
GitOps → Argo CD.

**Docs:** https://platform-pipelines.github.io/platform-pipeline-library/

A consuming repo's Jenkinsfile is two lines:

```groovy
@Library('platform-pipeline@main') _
standardPipeline()
```

Everything else is declared in `.ci/config.yaml`. Start from the example
closest to your repo. Each one is a complete repo root — `.ci/config.yaml`,
Jenkinsfile(s), source, tests, Dockerfile or IaC — that passes its own lint,
tests and coverage floor, so copying the directory gives a buildable repo:

| Example | Entry points | Shape |
|---|---|---|
| [`examples/go-service`](examples/go-service) | `standardPipeline`, `cdPipeline` | Go app, kaniko-docker, three gitops environments, CD promotion to prod |
| [`examples/java-service`](examples/java-service) | `standardPipeline` | Gradle app (Checkstyle, SpotBugs, JaCoCo), kaniko-k8s, Nexus publish |
| [`examples/node-service`](examples/node-service) | `standardPipeline` | npm app, ESLint + Prettier with autofix, node:test coverage |
| [`examples/python-service`](examples/python-service) | `standardPipeline` | Flask app in a venv, ruff + mypy + pytest, buildah |
| [`examples/cd-promotion`](examples/cd-promotion) | `cdPipeline` | **CD**: promote dev → staging → prod or roll back, no rebuild |
| [`examples/aws-ecs-service`](examples/aws-ecs-service) | `standardPipeline`, `cdPipeline` | **AWS end to end**: ECR push via STS, ECS Fargate rollout with rollback, cross-account prod |
| [`examples/terraform-aws-platform`](examples/terraform-aws-platform) | `standardPipeline`, `terraformDriftPipeline` | **Terraform end to end**: state bootstrap, VPC/ALB/ECS/ECR, mocked tests, conftest per plan, approvals, nightly drift |
| [`examples/terraform-stack`](examples/terraform-stack) | `standardPipeline` | Terraform VPC: plan → approve → apply |
| [`examples/cloudformation-stack`](examples/cloudformation-stack) | `standardPipeline` | CloudFormation: change set → approve → execute |

Every example config is loaded by `ExamplesConfigTest`, and every Jenkinsfile
is checked to call a real entry point, so they cannot drift from what the
library accepts.

## Pipelines

| Entry point | Job type | What it does |
|---|---|---|
| `standardPipeline()` | Multibranch | CI and deploy: lint → build → test → scan → package/image → deploy per environment |
| `cdPipeline()` | Pipeline (`ENVIRONMENT`, `IMAGE_TAG`, `DRY_RUN`) | CD only: promote the tag the `promoteFrom` environment runs, or deploy/roll back a given tag; checks the tag exists first |
| `terraformDriftPipeline(schedule: '…')` | Pipeline (cron) | Plans every Terraform environment against real state; UNSTABLE + Slack on drift; never applies |

Deploy strategies (`deployStrategy`): `gitops` (default for apps — commit the
tag, Argo CD syncs), `ecs` (register a task definition revision, update the
service, wait, roll back on failure), `terraform` and `cloudformation`
(derived from `buildTool`).

## Agents

Set `CI_TOOLBOX_IMAGE` on the controller (the local stack does, see
`local/casc/jenkins.yaml`) to the published [toolbox](toolbox/) image. Build
steps then run inside it on any docker agent; without it they fall back to
stock language images, which lack golangci-lint, ruff, tflint, conftest,
cfn-lint, checkov and python3. Agents that *are* the toolbox set
`CI_TOOLBOX=true` and run steps in place.

## Config reference

Only `appName`, `buildTool`, and `imageRepo` (when `containerize: true`) are
required. Everything else defaults from
[`configDefaults`](vars/configDefaults.groovy); environment entries default
from [`configEnvDefaults`](vars/configEnvDefaults.groovy).

| Section | Keys |
|---|---|
| top level | `appName`, `buildTool`, `runtimeVersion`, `imageRepo`, `containerize` (true), `dockerfile` (`Dockerfile`), `imageBuilder` (`kaniko-docker`), `gitopsRepo`, `gitopsBranch` (`main`), `deployStrategy` (derived) |
| `lint` | `enabled` (true), `failOnError` (true), `autoFormat` (false) |
| `quality` | `sonar` (true), `sonarProjectKey` (appName), `sonarSources` (`.`), `sonarExclusions`, `failOnQualityGate` (true), `trivy` (true), `trivyFailOn` (HIGH, CRITICAL), `trivyIgnoreUnfixed` (true), `secretScan` (true), `dependencyCheck` (false), `dependencyCheckCvss` (7), `minCoverage`, `sbom` (true), `signImage` (false) |
| `publish` | `nexusRepo` — Nexus repository for build artifacts; publishing is skipped when unset |
| `approval` | `allowSelfApproval` (false) |
| `notify` | `slackChannel`, `on` (`change` — or `always` / `failure`), `githubChecks` (true) |
| `infra` | `workingDir` (`.`), `varFiles`, `backendConfig`, `policyDir`, `region` (`us-east-1`), `assumeRole`, `awsCredentialsId` (`aws-credentials`), `template`, `templates`, `artifactBucket`, `capabilities` |
| `environments[]` | `name`, `namespace` (name), `manifestPath`, `branchPattern` (`main`), `requiresApproval`, `approvers`, `approvalTimeoutMinutes` (60), `promoteFrom`, `ecsCluster`, `ecsService`, `ecsContainer` (appName), `workspace`, `backendConfig`, `varFiles`, `stackName`, `parameters`, `region`, `assumeRole`, `awsCredentialsId` |
| `extra` | free-form — never read or checked by the pipeline; use it for your own tooling |

Allowed values live in one place each: `configSupportedTools` (buildTool),
`configImageBuilders`, `configDeployStrategies`.

**Typos are reported.** A key the library does not recognise is logged at
Init, with a hint:

```
[WARN]  unknown config key 'quality.minCoverge' — did you mean 'minCoverage'? (in .ci/config.yaml)
```

Unknown keys warn; invalid values (an unsupported `buildTool`, a missing
`imageRepo`, ...) fail the build with every problem listed at once.

**Init prints what the build will do** — deploy strategy, image builder, lint
mode, enabled gates, coverage floor, and the environments this branch reaches.

### Migrating from `extra.*`

These keys used to be read out of `extra`. They still work, with a
deprecation warning, until you move them:

| Old | New |
|---|---|
| `extra.nexusRepo` | `publish.nexusRepo` |
| `extra.sonarSources` | `quality.sonarSources` |
| `extra.sonarExclusions` | `quality.sonarExclusions` |
| `extra.dependencyCheckCvss` | `quality.dependencyCheckCvss` |
| `extra.allowSelfApproval` | `approval.allowSelfApproval` |

If both are set, the new key wins. The mapping lives in
[`configDeprecatedKeys`](vars/configDeprecatedKeys.groovy).

## Design rules

**No `src/` classes.** Everything is a `vars/` script. Jenkins serialises
pipeline state between steps, and `src/` classes must implement `Serializable`
and avoid non-serialisable fields or the build dies mid-run with
`NotSerializableException`. Plain scripts sidestep that whole category, and
they load directly in tests.

**One function per file.** Every file in `vars/` defines exactly one `call()`.
The filename is the step name. Nothing is hidden inside a helper method that
you have to open a file to discover.

**Config is data.** `configLoad` returns a plain `Map`. Every other step reads
that map. A config can be printed, diffed, and validated with no Jenkins.

**Parsing lives in Python, not Groovy strings.** Anything needing real logic —
coverage maths, JSON walking — is a file in `resources/com/platformpipelines/scripts/`
that runs and tests standalone. Groovy stays at the level of "run this, check
the exit code".

**Tools are containers, not plugins.** Sonar, Trivy, Gitleaks, Kaniko, Argo
are containers or REST calls. `plugins.txt` stays at a dozen entries and the
same commands run on a laptop.

**Jenkins never touches the cluster.** The last CI step commits an image tag to
the GitOps repo. Argo converges. Rollback is `git revert`.

## Stages

```
Init → Lint → Build → Test → Quality & Security → Package → Scan Image → Deploy
                                    (parallel)       │                    (per env)
                                                     └─ build · SBOM · sign
```

## Supply chain

- **SBOM** — CycloneDX, generated by Trivy, archived and fingerprinted. This is
  what lets you answer "are we affected by this CVE" across every deployed
  service in minutes rather than rebuilding each one.
- **Signing** — cosign keyless, signing the digest not the tag, with the SBOM
  attached as an attestation. Only meaningful alongside an admission policy
  (Kyverno, Sigstore policy-controller) that rejects unsigned images.
- **Audit trail** — `.ci-audit.jsonl`, archived every run.

## Infrastructure as code

`buildTool: terraform` or `cloudformation` switches the pipeline into an infra
shape. `containerize`, `deployStrategy`, SBOM and signing are all derived, so
an IaC repo does not restate them.

What changes:

- **Security scan** — `trivy config` over the templates instead of a
  dependency scan. Trivy absorbed tfsec, so this is one tool, not another one.
- **No image, no SBOM, no publish** — nothing is built.
- **Deploy** — plan, then approve, then apply.

The property that matters: **apply consumes the exact plan that was approved.**
Terraform applies the saved `tfplan` file; CloudFormation executes the change
set created before the gate. Neither re-computes a diff after approval. This
is the classic infra pipeline bug — you approve plan A and apply plan B,
because the world moved in between — and both paths are built to make it
impossible rather than unlikely.

The approval also sits in a different place per strategy. For gitops the gate
comes before the manifest commit, because once committed Argo acts on it. For
infra the gate sits between plan and apply, so the approver sees the actual
diff rather than a general intention to deploy.

Plan summaries call out destructive changes explicitly and post to the PR:

```
1 to create, 1 to update, 1 to replace, 1 to delete | DESTRUCTIVE: aws_db_instance.main, aws_security_group.old
```

## Image builders

`buildImage` routes on `imageBuilder:` because how you build without a root
daemon depends on where agents run.

| mode | needs | trade-off |
|---|---|---|
| `kaniko-docker` (default) | pinned plugin set | needs a docker socket on the agent |
| `kaniko-k8s` | `kubernetes` plugin + pod template | no socket anywhere; the only truly unprivileged option |
| `buildah` | toolbox agent, subuid + fuse-overlayfs | no daemon, but host setup required |

## Language steps

Each language gets four explicit steps plus its metadata. Nothing is shared
except the dispatcher, so changing Go's lint rules cannot affect Node.

| | lint | build | test | package |
|---|---|---|---|---|
| Go | `goLint` | `goBuild` | `goTest` | `goPackage` |
| Python | `pythonLint` | `pythonBuild` | `pythonTest` | `pythonPackage` |
| Java (Maven) | `mavenLint` | `mavenBuild` | `mavenTest` | `mavenPackage` |
| Java (Gradle) | `gradleLint` | `gradleBuild` | `gradleTest` | `gradlePackage` |
| Node | `nodeLint` | `nodeBuild` | `nodeTest` | `nodePackage` |
| docker-only | `dockerOnlyLint` | — | — | — |
| Terraform | `terraformLint` | `terraformBuild` | `terraformTest` | `terraformPackage` |
| CloudFormation | `cfnLint` | `cfnBuild` | `cfnTest` | `cfnPackage` |

What each lint step runs:

- **Go** — `gofmt -l`, `go vet`, `golangci-lint`
- **Python** — `ruff check`, `ruff format --check`, `mypy` when configured
- **Java** — Checkstyle and SpotBugs
- **Node** — ESLint, Prettier, and `tsc --noEmit` when `tsconfig.json` exists
- **docker-only** — hadolint against the Dockerfile
- **Terraform** — `fmt -check`, `validate`, tflint
- **CloudFormation** — cfn-lint, then `aws cloudformation validate-template`

`lint.failOnError: false` turns any of them into report-only. The Node
typecheck is the exception — type errors always fail, because shipping a
TypeScript build that does not typecheck is not a style preference.

Dispatchers: `lintApp`, `buildApp`, `testApp`, `packageApp` route on
`cfg.buildTool` and fail loudly on an unknown value rather than skipping.

Metadata: `appToolImage`, `appTestReport`, `appCoverageFile`, `appArtifacts`,
`appCacheDir`, `appLintReport`, `appSonarProps`.

## Adding a language

1. Write `rustLint`, `rustBuild`, `rustTest`, `rustPackage`.
2. Add a case to each of the four dispatchers.
3. Add a case to each `app*` metadata step.
4. Add `rust` to `configSupportedTools`.

`AppMetadataTest` walks `configSupportedTools`, so a missing metadata case
fails the build rather than silently producing a stage that does nothing.

## Adding a step or a config key

- **A step:** add `vars/<name>.groovy` with a usage comment, plus
  `docs/<section>/<name>.md` and an entry in `mkdocs.yml`'s nav.
  `DocsCoverageTest` fails the build if any of the three is missing.
- **A config key:** add it to `configDefaults` (or `configEnvDefaults`), even
  if its default is `null`. That map is the schema: keys not listed there are
  reported as unknown.
- **Renaming a key:** add the old → new mapping to `configDeprecatedKeys`, so
  existing repos keep working and get told where the key moved.

## Local development

### Running the tests

```bash
make test    # Groovy / jenkins-pipeline-unit suite      (./gradlew test)
make lint    # CodeNarc over vars/ and test/             (./gradlew codenarcMain codenarcTest)
make check   # test + lint — exactly what CI runs
```

No Jenkins instance. `BaseTest` registers every `vars` file as a callable step
so cross-step calls resolve, and records shell commands so tests assert on what
would have run.

CodeNarc's ruleset lives at `config/codenarc/codenarc.groovy` and is zero
tolerance for priority-1/2 violations — `make check` fails the same way CI's
`.github/workflows/ci.yml` does.

Groovy 3.0.19 (this project's compiler) cannot read class files newer than
Java 17. On macOS, the Makefile auto-detects a JDK 17 via
`/usr/libexec/java_home -v 17` and exports `JAVA_HOME` for you. On other
platforms, point `JAVA_HOME` at a JDK 17 yourself before running `make test`
or `./gradlew`.

The Python resource scripts are testable on their own:

```bash
python3 resources/com/platformpipelines/scripts/coverage_percent.py coverage.xml
```

`coverage_percent.py` handles cobertura, JaCoCo, Go coverprofile, and lcov —
all four verified against real report samples.

`manifestBumpImage` gets the most attention. A pattern that fails to match
means the pipeline goes green while the old image stays deployed, so it is
covered against kustomize, plain manifests, Helm values, registries with ports,
inline list items, and the already-current no-op.

### CI toolbox

The pipeline shells out to Java, Node, Go, Python, Trivy, Gitleaks, hadolint,
and more, all bundled into one `ci-toolbox` image (see `toolbox/`) so the
same tool versions run in CI and on a laptop.

```bash
make toolbox-build    # build ci-toolbox:local
make toolbox-verify   # build, then run toolbox/verify.sh inside it
```

`toolbox-verify` smoke-tests that every tool the pipeline depends on exists
at the expected version inside the image — run it after bumping any tool
version in `toolbox/Dockerfile`.

### Local stack

```bash
make local-up        # build and start Jenkins + SonarQube + Nexus in the background
make local-logs       # follow logs for all services
make local-down       # stop the stack, keep data volumes
make local-clean      # stop the stack and delete data volumes
make local-restart    # local-down + local-up
```

Set `GITHUB_TOKEN`, `SONAR_TOKEN`, and `SLACK_WEBHOOK` in your environment
before `make local-up` if you want those integrations to work against the
local stack. Every credential in the table below is wired from an environment
variable in `local/casc/jenkins.yaml`.

Jenkins :8080, SonarQube :9000 (admin/admin), Nexus :8081. Jenkins builds
from `local/plugins.txt` — pinned, no UI installs.

## Versioning this library

Consuming repos currently point at `@Library('platform-pipeline@main')` (see
`examples/`). Floating on `main` means every merge to this repo reaches every
pipeline on its next build — convenient while the library is young and
changing fast, but it also means one bad merge breaks every pipeline at once.

Once the library and its consumers stabilize, switch to pinned release tags
instead (`@Library('platform-pipeline@v2')`): PR → unit tests → merge → tag
`v2.x` → smoke test one canary repo → move consumers to the new tag →
announce. Tags are the safer default for a mature library; `main` is a
deliberate, temporary trade-off, not the long-term plan.

## Credentials

| ID | Kind | Used by |
|---|---|---|
| `github-token` | string | statuses, PR comments, GitOps commits |
| `sonar-token` | string | scan and quality gate |
| `slack-webhook` | string | notifications |
| `argocd-token` | string | sync wait |
| `ghcr-credentials` | username/password | registry push (override with `REGISTRY_CREDENTIALS_ID`) |
| `nexus-credentials` | username/password | artifact publish (`publish.nexusRepo`) |
| `aws-credentials` | username/password (access key id / secret) | Terraform and CloudFormation; override per repo or environment with `awsCredentialsId` |
| `cosign-oidc-token` | string | keyless image signing (`quality.signImage`) |

## Environment

Set on the controller via JCasC: `SONAR_HOST_URL`, `NEXUS_URL`,
`ARGOCD_SERVER`. Optional: `PIPELINE_DEBUG=true`, `GITHUB_CREDENTIALS_ID`,
`REGISTRY_CREDENTIALS_ID`, `GITHUB_API_URL` for Enterprise.
