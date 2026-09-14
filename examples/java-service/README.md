# java-service example (Gradle)

A full end-to-end example config for a containerized Java service built with
**Gradle**. For Maven, set `buildTool: maven` — the library treats both
identically: same stages, same dispatcher, different four steps underneath
(`gradleLint`/`gradleBuild`/`gradleTest`/`gradlePackage` instead of the
`maven*` equivalents).

## Adapting this to a real repo

1. Copy `Jenkinsfile` to the repo root.
2. Copy `config.yaml` to `.ci/config.yaml`.
3. Replace `appName`, `imageRepo`, `gitopsRepo`, and every `manifestPath` /
   `namespace` with real values.
4. Update `quality.sonarProjectKey`, Slack channel, and approver list.

## What each section demonstrates

- **buildTool: gradle** — routes through `gradleLint`/`gradleBuild`/
  `gradleTest`/`gradlePackage`; `appToolImage` picks the matching
  `gradle:9-jdk21` container when the agent isn't already the toolbox image.
- **containerize + imageBuilder: kaniko-k8s** — image build with no docker
  socket anywhere, for agents running on locked-down Kubernetes nodes.
- **lint.failOnError: true** — Checkstyle/SpotBugs violations fail the build
  instead of just reporting.
- **quality** — every supply-chain knob on at once: Sonar quality gate,
  Trivy dependency scan, secret scanning, OWASP dependency-check, a 75%
  coverage floor, SBOM generation, and cosign image signing.
- **environments** — three gitops environments showing the full range: an
  open `dev` (`branchPattern: "*"`), a `staging` that only follows `main`,
  and a `prod` that additionally requires approval with named approvers and
  a timeout.
- **notify** — Slack notification only `on: change`, plus GitHub status
  checks.
- **publish.nexusRepo** — Nexus is still the JVM artifact target even though
  the build tool is Gradle, not Maven.
- **approval.allowSelfApproval** — left `false`, so whoever triggered a build
  cannot approve its prod deploy.
