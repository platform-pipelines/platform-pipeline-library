# useScript

Writes a bundled resource script to the workspace and returns its path.
Keeps parsing logic in real `.py` files that can be tested on their own,
instead of heredocs buried inside Groovy strings.

## Syntax

```groovy
useScript(String name)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `name` | `String` | yes | — | Filename under `resources/com/platformpipelines/scripts/`. |

## Returns

The workspace-relative path `.ci-scripts/<name>`. The file is only written if
it isn't already there, so repeated calls in one workspace are cheap.

## Bundled scripts

| Script | Arguments | Prints | Used by |
|---|---|---|---|
| `coverage_percent.py` | `<report>` | `82.4` | [coveragePercent](../quality/coveragePercent.md) |
| `trivy_summary.py` | `<trivy.json>` | `CRITICAL=1, HIGH=3` or `clean` | [trivySummary](../quality/trivySummary.md) |
| `sbom_summary.py` | `<sbom.cdx.json>` | `214 components (pypi=180, deb=34)` | [generateSbom](../images/generateSbom.md) |
| `sonar_gate.py` | `<ceTaskUrl> <host> <token>` | `OK`, `ERROR`, `WARN` or `PENDING` | [sonarWaitForGate](../quality/sonarWaitForGate.md) |
| `terraform_plan_summary.py` | `<plan.json>` | `1 to create, 1 to delete \| DESTRUCTIVE: …` | [terraformPlan](../infrastructure/terraformPlan.md) |
| `cfn_changeset_summary.py` | `<changeset.json>` | `2 to add, 1 to modify` / `no changes` / `failed: …` | [cfnChangeSet](../infrastructure/cfnChangeSet.md) |
| `eslint_to_junit.py` | stdin: ESLint JSON | JUnit XML | [nodeLint](../languages/nodeLint.md) |
| `github_file_sha.py` | stdin: contents API JSON | blob sha or empty | [githubFileSha](../github-slack/githubFileSha.md) |
| `find_pr_comment.py` | `<marker>`; stdin: comments JSON | comment id or empty | [githubFindComment](../github-slack/githubFindComment.md) |

## Examples

```groovy
def script = useScript('coverage_percent.py')      // → '.ci-scripts/coverage_percent.py'
def pct = sh(script: "python3 ${script} coverage.xml", returnStdout: true).trim()   // → '82.4'
```

```groovy
sh "python3 ${useScript('trivy_summary.py')} trivy-image.json"
// → CRITICAL=1, HIGH=3
```

Running the same script on a laptop, outside Jenkins:

```bash
python3 resources/com/platformpipelines/scripts/coverage_percent.py coverage.xml
```

## Source

[`vars/useScript.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/useScript.groovy)
