# useScript

Writes a bundled resource script to the workspace and returns its path.
Keeps parsing logic in real `.py` files that can be tested on their own,
instead of heredocs buried inside Groovy strings.

## Signature

```groovy
def call(String name)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `name` | `String` | Filename under `resources/com/company/scripts/`. |

## Returns

Workspace-relative path the script was written to (cached across calls, so
it's only written once per build).

## Usage

```groovy
sh "python3 ${useScript('coverage_percent.py')} coverage.xml"
```

Used wherever the library shells out to one of its bundled Python scripts —
[`githubFileSha`](githubFileSha.md), [`githubFindComment`](githubFindComment.md),
and the coverage/scan-summary steps in [CI/CD](../ci-cd/index.md).

## Source

[`vars/useScript.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/useScript.groovy)
