# versionShortSha

Short commit sha. Reads `GIT_COMMIT` when Jenkins provides it to avoid a
subprocess on every call.

## Syntax

```groovy
versionShortSha()          // 7 characters
versionShortSha(int len)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `len` | `int` | no | `7` | Number of characters to keep. |

## Returns

The first `len` characters of `env.GIT_COMMIT`, or of `git rev-parse HEAD`
when `GIT_COMMIT` is unset.

## Examples

With `GIT_COMMIT=ab12cd3ef4567890ab12cd3ef4567890ab12cd3e`:

```groovy
versionShortSha()        // → 'ab12cd3'
versionShortSha(12)      // → 'ab12cd3ef456'
```

```groovy
env.GIT_SHORT_SHA = versionShortSha()
currentBuild.description = "python · ${env.GIT_SHORT_SHA}"
```

## How it fits

Used by [`versionResolve`](versionResolve.md), and stored by
[initPipeline](../pipelines/initPipeline.md) as `env.GIT_SHORT_SHA`, which feeds
image tags ([imageExtraTags](../images/imageExtraTags.md)), change set names
([cfnChangeSet](../infrastructure/cfnChangeSet.md)) and STS session names
([assumeAwsRole](../infrastructure/assumeAwsRole.md)).

## Source

[`vars/versionShortSha.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/versionShortSha.groovy)
