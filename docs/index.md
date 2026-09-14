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
2. Add `.ci/config.yaml` — see the full end-to-end examples for
   [Go](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/go-service),
   [Java](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/java-service),
   [Python](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/python-service),
   [Node](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/node-service),
   [Terraform](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/terraform-stack),
   and [CloudFormation](https://github.com/platform-pipelines/platform-pipeline-library/tree/main/examples/cloudformation-stack).
   Only `appName`, `buildTool` and (for container builds) `imageRepo` are
   required; everything else has a default.
3. [`configLoad`](other/configLoad.md) reads that file, layers it over
   [`configDefaults`](other/configDefaults.md), and validates it via
   [`configValidate`](other/configValidate.md) — start there if you want to
   understand exactly what fields are recognized. A misspelled key is logged
   with a "did you mean" hint by [`configUnknownKeys`](other/configUnknownKeys.md),
   and the first lines of the Init stage summarise what the build will do.

For the design rationale (why no `src/` classes, why config is data, why
parsing lives in Python), see the root
[README](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/README.md).

## Running this site locally

```bash
uv venv .venv-docs
uv pip install --python .venv-docs/bin/python -r docs/requirements.txt
uv run --python .venv-docs/bin/python mkdocs serve
```

or via the Makefile: `make docs-serve`.
