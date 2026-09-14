# githubComment

Adds a PR comment. No-op outside PR builds.

!!! note "Public API, not used internally"
    `standardPipeline` doesn't call this — it calls
    [`githubUpsertComment`](githubUpsertComment.md) instead, because that one
    replaces its own previous comment rather than accumulating. `githubComment`
    is kept as public API for consumers who want a one-off comment from a
    custom stage in their own `Jenkinsfile`.

## Syntax

```groovy
githubComment(String markdown)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `markdown` | `String` | yes | — | Comment body; GitHub-flavoured Markdown. |

Reads `env.CHANGE_ID` (the PR number) and the repo from
[`githubRepoSlug`](githubRepoSlug.md).

## Returns

Nothing. On a branch build (no `env.CHANGE_ID`) it logs
`Not a PR build — skipping comment` at debug level and returns. A failed API
call logs a warning but does not fail the build.

## Examples

```groovy
githubComment('Deployed to staging.')
```

Multi-line Markdown from a custom stage:

````groovy
stage('Smoke test') {
    steps {
        script {
            sh './scripts/smoke.sh > smoke.txt'
            githubComment("""### Smoke test
✅ passed against `${env.APP_VERSION}`

<details><summary>Output</summary>

```
${readFile('smoke.txt').take(3000)}
```
</details>""")
        }
    }
}
````

On PR `#87` of `acme/orders-api` this posts to
`POST /repos/acme/orders-api/issues/87/comments`.

## Source

[`vars/githubComment.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/githubComment.groovy)
