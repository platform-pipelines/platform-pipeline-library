# initPipeline

Checkout, load config, and set the version environment variables — the
`Init` stage in one call.

## Syntax

```groovy
def cfg = initPipeline(Map overrides)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `overrides` | `Map` | yes (may be `[:]`) | — | Inline config overrides, passed to [`configLoad`](../other/configLoad.md). May include `configFile`. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `notify.githubChecks` | `true` | `false` | Posts a `ci/jenkins` pending status. |
| `quality.*`, `lint.*`, `environments` | see [configDefaults](../other/configDefaults.md) | | Printed in the build summary line. |

## Returns

The loaded, validated config `Map`. Also sets:

| Variable / field | Sample value | Source |
|---|---|---|
| `env.APP_NAME` | `orders-api` | `appName` |
| `env.APP_VERSION` | `1.4.0` | [versionResolve](../other/versionResolve.md) |
| `env.IMAGE_TAG` | `1.4.0` | [versionImageTag](../other/versionImageTag.md) |
| `env.GIT_SHORT_SHA` | `ab12cd3` | [versionShortSha](../other/versionShortSha.md) |
| `currentBuild.displayName` | `#42 1.4.0` | |
| `currentBuild.description` | `python · ab12cd3` | |

## Examples

```groovy
node('linux') {
    def cfg = initPipeline([:])
    echo "Building ${env.APP_NAME} ${env.APP_VERSION}"
}
```

With overrides:

```groovy
def cfg = initPipeline([configFile: '.ci/nightly.yaml', quality: [dependencyCheck: true]])
```

Log output on `main` for the sample config:

```
====================================================================
  Initialise
====================================================================
[INFO]  Loaded pipeline config from .ci/config.yaml
[INFO]  app=orders-api version=1.4.0 tool=python
[INFO]  deploy=gitops image=kaniko-docker lint=blocking gates=sonar,trivy,secretScan,sbom minCoverage=75 environments=dev->prod
[AUDIT] pipeline.start [version:1.4.0]
```

## How it fits

Called from the `Init` stage of [standardPipeline](standardPipeline.md).
Runs `checkout scm` itself, so use it in pipelines with
`skipDefaultCheckout(true)`.

## Source

[`vars/initPipeline.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/initPipeline.groovy)
