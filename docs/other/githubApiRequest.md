# githubApiRequest

One curl-based REST call to GitHub. Returns `true` on success — a non-2xx
logs and returns `false` rather than failing the build, since a flaky status
update shouldn't sink a green build. Callers that must not fail silently
(e.g. [`githubCommitFile`](githubCommitFile.md)) check the result themselves.

!!! note "Hardened against shell injection"
    `method` is validated against an allowlist (`GET`/`POST`/`PUT`/`PATCH`/`DELETE`)
    and the request URL (`path` appended to [`githubApiUrl`](githubApiUrl.md))
    is shell-quoted before reaching `curl`, so a malicious `path` can't break
    out of its argument.

## Signature

```groovy
def call(Map args)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `args.method` | `String` | HTTP verb; must be `GET`/`POST`/`PUT`/`PATCH`/`DELETE` (default `POST`). |
| `args.path` | `String` | API path appended to [`githubApiUrl`](githubApiUrl.md). |
| `args.body` | `String` | JSON request body (default `'{}'`). |
| `args.description` | `String` | Label used in log messages. |

## Returns

`true` on a response below 300, `false` otherwise.

## Usage

```groovy
githubApiRequest(method: 'POST', path: '/repos/acme/api/issues/1/comments', body: json)
```

The base call every other GitHub step ([`githubComment`](githubComment.md),
[`githubCommitFile`](githubCommitFile.md), [`githubSetStatus`](githubSetStatus.md),
[`githubUpsertComment`](githubUpsertComment.md)) builds on.

## Source

[`vars/githubApiRequest.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/githubApiRequest.groovy)
