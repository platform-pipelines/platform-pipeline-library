# githubFindComment

Id of the existing PR comment carrying a hidden marker, or empty string.

## Syntax

```groovy
githubFindComment(String marker)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `marker` | `String` | yes | — | Hidden text that identifies the comment, conventionally an HTML comment such as `<!-- platform-pipeline:scan-summary -->`. |

Reads `env.CHANGE_ID` (the PR number) and the repo from
[`githubRepoSlug`](githubRepoSlug.md). Only call it on PR builds.

## Returns

The comment id as a `String`, or `''` if no comment on the PR contains the
marker. Only the first 100 comments are searched.

## Examples

```groovy
githubFindComment('<!-- platform-pipeline:scan-summary -->')   // → '1873345123'
githubFindComment('<!-- my-team:perf-report -->')              // → ''  (not posted yet)
```

Edit-or-create by hand:

```groovy
def marker = '<!-- my-team:perf-report -->'
def id = githubFindComment(marker)
def body = groovy.json.JsonOutput.toJson([body: "${marker}\n\np95 latency: 212 ms"])

if (id) {
    githubApiRequest(method: 'PATCH', path: "/repos/${githubRepoSlug()}/issues/comments/${id}", body: body)
} else {
    githubApiRequest(method: 'POST', path: "/repos/${githubRepoSlug()}/issues/${env.CHANGE_ID}/comments", body: body)
}
```

That is exactly what [`githubUpsertComment`](githubUpsertComment.md) does, so
prefer calling it.

## How it fits

Parses the PR comments list via the bundled `find_pr_comment.py` script (see
[`useScript`](useScript.md)). Used by [`githubUpsertComment`](githubUpsertComment.md).

## Source

[`vars/githubFindComment.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/githubFindComment.groovy)
