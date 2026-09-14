# platform-pipeline

Reference documentation for the `platform-pipeline` Jenkins shared library —
GitHub → lint → build → test → scan → GHCR → GitOps → Argo CD, driven by one
`.ci/config.yaml` per consuming repo.

```groovy
@Library('platform-pipeline@main') _
standardPipeline()
```

This site documents every callable step in [`vars/`](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/vars),
grouped by what it does rather than alphabetically:

- **[CI/CD](ci-cd/index.md)** — language builds and tests, container images,
  quality/security scanning, and the pipeline orchestration steps that tie
  them together.
- **[Cloud](cloud/index.md)** — AWS credentials, CloudFormation, Terraform,
  and GitOps/Argo CD deployment.
- **[Other](other/index.md)** — config loading and validation, GitHub API
  integration, Slack notifications, logging, versioning, and small shared
  utilities.

## Getting started

1. Drop a two-line `Jenkinsfile` (above) in your repo.
2. Add `.ci/config.yaml`. The smallest valid config is:

    ```yaml
    appName: orders-api
    buildTool: python          # maven | gradle | npm | python | go | docker-only | terraform | cloudformation
    imageRepo: ghcr.io/acme/orders-api
    ```

    Only `appName`, `buildTool` and (for container builds) `imageRepo` are
    required; everything else has a default. For complete configs see the
    end-to-end examples for
    [Go](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/go-service),
    [Java](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/java-service),
    [Python](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/python-service),
    [Node](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/node-service),
    [Terraform](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/terraform-stack),
    and [CloudFormation](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/cloudformation-stack).
3. [`configLoad`](other/configLoad.md) reads that file, layers it over
   [`configDefaults`](other/configDefaults.md), and validates it via
   [`configValidate`](other/configValidate.md) — start there if you want to
   understand exactly what fields are recognized. A misspelled key is logged
   with a "did you mean" hint by [`configUnknownKeys`](other/configUnknownKeys.md),
   and the first lines of the Init stage summarise what the build will do.

For the design rationale (why no `src/` classes, why config is data, why
parsing lives in Python), see the root
[README](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/README.md).

## How to read a step page

Every step page uses the same layout:

| Section | What it tells you |
|---|---|
| **Syntax** | Every way the step can be called, copy-pasteable. `[ ]` in a comment marks an optional argument. |
| **Parameters** | Each argument with its type, whether it is required, and its default. |
| **Config keys read** | For steps that take `cfg`: the `.ci/config.yaml` keys the step actually reads, their defaults, and a sample value. |
| **Returns** | The return value and any side effects (environment variables set, files archived, build failures). |
| **Examples** | Realistic calls with sample values and the result they produce. |

Steps are called from a `Jenkinsfile` or from inside `script { }` in a
declarative pipeline. Groovy lets you drop the parentheses for a single
argument, so `logInfo 'hi'` and `logInfo('hi')` are the same call. Named
arguments (`scanTrivy(cfg: cfg, type: 'fs')`) arrive as a single `Map`.

### Sample objects used throughout

Most steps take the loaded pipeline config (`cfg`) and, for deploy steps,
one environment entry from it (`envCfg`). The examples on every page assume
this config:

```yaml
# .ci/config.yaml
appName: orders-api
buildTool: python
runtimeVersion: "3.12"
imageRepo: ghcr.io/acme/orders-api
gitopsRepo: acme/gitops-manifests

quality:
  minCoverage: 75

notify:
  slackChannel: "#orders-ci"

environments:
  - name: dev
    manifestPath: apps/orders-api/dev/kustomization.yaml
    branchPattern: "*"
  - name: prod
    manifestPath: apps/orders-api/prod/kustomization.yaml
    requiresApproval: true
    approvers: [platform-leads]
```

```groovy
def cfg    = configLoad()           // the whole merged config (defaults filled in)
def envCfg = cfg.environments[1]    // [name: 'prod', namespace: 'prod', requiresApproval: true, ...]
```

…and this build environment, set by Jenkins and [`initPipeline`](ci-cd/initPipeline.md):

| Variable | Sample value |
|---|---|
| `env.BRANCH_NAME` | `main` |
| `env.BUILD_NUMBER` | `42` |
| `env.GIT_COMMIT` | `ab12cd3ef4567890ab12cd3ef4567890ab12cd3e` |
| `env.GIT_SHORT_SHA` | `ab12cd3` |
| `env.APP_VERSION` | `1.4.0` |
| `env.IMAGE_TAG` | `1.4.0` |
| `env.CHANGE_ID` | unset on branch builds; the PR number (e.g. `87`) on PR builds |

Infrastructure pages (Terraform/CloudFormation) use the
[terraform-stack](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/terraform-stack)
and [cloudformation-stack](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/cloudformation-stack)
example configs instead, and say so.

## Running this site locally

```bash
make docs-serve
```

That installs the docs dependencies into `.venv-docs` with `uv` and serves
the site with live reload. Run the same steps by hand with:

```bash
uv venv .venv-docs
uv pip install --python .venv-docs/bin/python -r docs/requirements.txt
uv run --python .venv-docs/bin/python mkdocs serve
```
