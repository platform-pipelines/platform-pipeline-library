# githubApiRequest

One curl-based REST call to GitHub. Returns `true` on success — a non-2xx
logs and returns `false` rather than failing the build, since a flaky status
update shouldn't sink a green build. Callers that must not fail silently
(e.g. [`githubCommitFile`](githubCommitFile.md)) check the result themselves.

!!! note "Hardened against shell injection"
    `method` is validated against an allowlist (`GET`/`POST`/`PUT`/`PATCH`/`DELETE`)
    and both the body and the request URL (`path` appended to
    [`githubApiUrl`](githubApiUrl.md)) are shell-quoted before reaching
    `curl`, so a malicious `path` can't break out of its argument.

## Syntax

```groovy
githubApiRequest(
    method     : 'POST',          // [optional]
    path       : '/repos/<owner>/<repo>/...',
    body       : '<json string>', // [optional]
    description: '<log label>'    // [optional]
)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `method` | `String` | no | `POST` | HTTP verb: `GET`, `POST`, `PUT`, `PATCH` or `DELETE`. Anything else fails the build. |
| `path` | `String` | yes | — | API path appended to [`githubApiUrl`](githubApiUrl.md), starting with `/`. |
| `body` | `String` | no | `'{}'` | JSON request body. Build it with `groovy.json.JsonOutput.toJson(...)`. |
| `description` | `String` | no | `"<method> <path>"` | Label used in the success/failure log line. |

Authenticates with the string credential named by
[`githubCredentialsId`](githubCredentialsId.md) (default `github-token`).

## Returns

`true` when GitHub answers with a status below 300, `false` otherwise. On
failure the status code and response body are printed to the log.

## Examples

**Comment on a PR:**

```groovy
def json = groovy.json.JsonOutput.toJson([body: 'Deployed `1.4.0` to staging.'])

githubApiRequest(
    method     : 'POST',
    path       : '/repos/acme/orders-api/issues/87/comments',
    body       : json,
    description: 'PR comment'
)
// → true
// log (PIPELINE_DEBUG=true): [DEBUG] GitHub ok: PR comment
```

**Add a label, and act on failure:**

```groovy
def ok = githubApiRequest(
    path: "/repos/${githubRepoSlug()}/issues/${env.CHANGE_ID}/labels",
    body: groovy.json.JsonOutput.toJson([labels: ['deployed:staging']])
)
if (!ok) { unstable 'Could not label the PR' }
```

**Failure output:**

```
GitHub POST /repos/acme/orders-api/issues/87/comments -> 404
{"message":"Not Found", ...}
[WARN]  GitHub call failed: PR comment
```

## How it fits

The base call every other GitHub write step ([`githubComment`](githubComment.md),
[`githubCommitFile`](githubCommitFile.md), [`githubSetStatus`](githubSetStatus.md),
[`githubUpsertComment`](githubUpsertComment.md)) builds on.

## Source

[`vars/githubApiRequest.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/githubApiRequest.groovy)
