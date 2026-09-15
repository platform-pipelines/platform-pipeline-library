# githubCommitFile

Commits one file to another repo via the contents API, so the GitOps bump
needs no clone and no push credentials.

## Syntax

```groovy
githubCommitFile(
    repo   : '<owner>/<name>',
    path   : '<file path in repo>',
    content: '<new file content>',
    branch : 'main',                   // [optional]
    message: 'chore: update <path>'    // [optional]
)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `repo` | `String` | yes | — | `owner/name` of the target repo, e.g. `acme/gitops-manifests`. |
| `path` | `String` | yes | — | File path within the repo. Created if missing, replaced if present. |
| `content` | `String` | yes | — | New file content as plain text; base64-encoded before sending. |
| `branch` | `String` | no | `main` | Branch to commit to. |
| `message` | `String` | no | `chore: update <path>` | Commit message. |

The token from [`githubCredentialsId`](githubCredentialsId.md) needs
`contents:write` on the target repo.

## Returns

Nothing. **Fails the build** if the commit fails — unlike a status update, a
failed GitOps commit means the deploy did not happen, so it must not pass
quietly:

```
Failed to commit apps/orders-api/prod/kustomization.yaml to acme/gitops-manifests@main
```

## Examples

**Replace a file:**

```groovy
def yamlText = githubFetchFile(repo: 'acme/gitops-manifests',
                               path: 'apps/orders-api/prod/kustomization.yaml')

githubCommitFile(
    repo   : 'acme/gitops-manifests',
    path   : 'apps/orders-api/prod/kustomization.yaml',
    content: manifestBumpImage(yamlText, 'ghcr.io/acme/orders-api:1.4.0'),
    message: 'deploy(prod): orders-api 1.4.0'
)
```

**Create a new file on a non-default branch:**

```groovy
githubCommitFile(
    repo   : 'acme/release-notes',
    branch : 'drafts',
    path   : "orders-api/${env.APP_VERSION}.md",
    content: "# orders-api ${env.APP_VERSION}\n\nBuilt from ${env.GIT_COMMIT}\n"
)
// commit message → 'chore: update orders-api/1.4.0.md'
```

## How it fits

Looks up the existing blob sha via [`githubFileSha`](githubFileSha.md) first
— the contents API needs it to replace a file, and rejects it when creating
a new one — then calls [`githubApiRequest`](githubApiRequest.md) with `PUT`.
Used by [updateManifest](../deploy/updateManifest.md).

## Source

[`vars/githubCommitFile.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/githubCommitFile.groovy)
