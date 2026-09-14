# initPipeline

Checkout, load config, and set the version environment variables.

## Signature

```groovy
def call(Map overrides)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `overrides` | `Map` | Inline config overrides, passed through to [`configLoad`](../other/configLoad.md). |

## Returns

The loaded, validated pipeline config `Map`.

## Usage

```groovy
def cfg = initPipeline(overrides)
```

Sets `env.APP_NAME`, `env.APP_VERSION` (via [versionResolve](../other/versionResolve.md)),
`env.IMAGE_TAG` (via [versionImageTag](../other/versionImageTag.md)), and
`env.GIT_SHORT_SHA` (via [versionShortSha](../other/versionShortSha.md)).
Called from the `Init` stage of [standardPipeline](standardPipeline.md).

## Source

[`vars/initPipeline.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/initPipeline.groovy)
