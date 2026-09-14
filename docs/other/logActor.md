# logActor

Who triggered this build.

!!! note "Why not BUILD_USER"
    Walks the build causes chain rather than reading `BUILD_USER`, which
    only exists if you install the build-user-vars plugin — deliberately not
    in `plugins.txt`.

## Signature

```groovy
def call()
```

## Returns

The user id, a trigger name (`scm-trigger`/`timer`/`branch-indexing`), or
`'unknown'`.

## Usage

```groovy
def who = logActor()
```

Used by [`logAudit`](logAudit.md) and [`slackPayload`](slackPayload.md) to
attribute a build to whoever (or whatever) started it.

## Source

[`vars/logActor.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/logActor.groovy)
