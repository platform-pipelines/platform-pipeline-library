# githubFetchFile

Reads a single file from any repo without cloning it.

!!! note "Hardened against shell injection"
    `repo` is validated to match an `owner/name` shape before use, and the
    full request URL is shell-quoted as a single argument to `curl`, so a
    malicious `path` or `branch` can't break out of it.

## Syntax

```groovy
githubFetchFile(
    repo  : '<owner>/<name>',
    path  : '<file path in repo>',
    branch: 'main'                 // [optional]
)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `repo` | `String` | yes | — | `owner/name` of the repo; must match `^[\w.-]+/[\w.-]+$`. |
| `path` | `String` | yes | — | File path within the repo. |
| `branch` | `String` | no | `main` | Branch, tag or commit sha to read from. |

## Returns

The raw file content as a `String`. Fails the build if `repo` is malformed
(`githubFetchFile: invalid repo slug '…'`) or the request fails (e.g. the file
does not exist, `curl --fail` returns non-zero).

## Examples

```groovy
def yaml = githubFetchFile(repo: 'acme/gitops-manifests',
                           path: 'apps/orders-api/prod/kustomization.yaml')
// → "apiVersion: kustomize.config.k8s.io/v1beta1\nkind: Kustomization\n..."
```

Reading from a tag and parsing it:

```groovy
def text = githubFetchFile(repo: 'acme/platform-config', branch: 'v2.3.0', path: 'teams.yaml')
def teams = readYaml(text: text)
echo "owners: ${teams['orders-api'].owners}"
```

## How it fits

Used by [updateManifest](../ci-cd/updateManifest.md) to read the current
manifest before bumping the image.

## Source

[`vars/githubFetchFile.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/githubFetchFile.groovy)
