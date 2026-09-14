# githubCommitFile

Commits one file to another repo via the contents API, so the GitOps bump
needs no clone and no push credentials.

## Signature

```groovy
def call(Map args)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `args.repo` | `String` | `owner/name` of the target repo. |
| `args.branch` | `String` | Branch to commit to (default `main`). |
| `args.path` | `String` | File path within the repo. |
| `args.content` | `String` | New file content, base64-encoded before sending. |
| `args.message` | `String` | Commit message (default `"chore: update <path>"`). |

## Returns

Nothing. Throws if the commit fails — unlike a status update, a failed
GitOps commit means the deploy did not happen, so it must fail the build
rather than pass quietly.

## Usage

```groovy
githubCommitFile(repo: 'acme/gitops', path: 'apps/api/values.yaml', content: yamlText)
```

Looks up the existing blob sha via [`githubFileSha`](githubFileSha.md) first
— the contents API needs it to replace a file, and rejects it when creating
a new one — then calls [`githubApiRequest`](githubApiRequest.md) with `PUT`.

## Source

[`vars/githubCommitFile.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/githubCommitFile.groovy)
