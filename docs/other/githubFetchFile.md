# githubFetchFile

Reads a single file from any repo without cloning it.

!!! note "Hardened against shell injection"
    `repo` is validated to match an `owner/name` shape before use, and the
    full request URL is shell-quoted as a single argument to `curl`, so a
    malicious `path` or `branch` can't break out of it.

## Signature

```groovy
def call(Map args)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `args.repo` | `String` | `owner/name` of the repo; must match an `owner/name` shape. |
| `args.branch` | `String` | Branch or ref to read from (default `main`). |
| `args.path` | `String` | File path within the repo. |

## Returns

Raw file content as a `String`. Throws if `repo` is malformed or the request
fails.

## Usage

```groovy
def yaml = githubFetchFile(repo: 'acme/gitops', path: 'apps/api/values.yaml')
```

## Source

[`vars/githubFetchFile.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/githubFetchFile.groovy)
