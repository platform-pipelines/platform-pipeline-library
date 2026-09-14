# githubFindComment

Id of the existing bot comment carrying this marker, or empty string.

## Signature

```groovy
def call(String marker)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `marker` | `String` | Hidden marker text identifying the bot comment. |

## Returns

Comment id as a `String`, or `''` if no comment carries the marker.

## Usage

```groovy
def id = githubFindComment('<!-- sonar-report -->')
```

Parses the PR comments list via the bundled `find_pr_comment.py` script (see
[`useScript`](useScript.md)). Used by [`githubUpsertComment`](githubUpsertComment.md)
to decide whether to edit an existing comment or create a new one.

## Source

[`vars/githubFindComment.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/githubFindComment.groovy)
