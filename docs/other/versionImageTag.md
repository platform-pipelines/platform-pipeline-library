# versionImageTag

Same value as [`versionResolve`](versionResolve.md), constrained to
characters legal in an OCI image tag.

## Syntax

```groovy
versionImageTag()                  // uses env.BRANCH_NAME
versionImageTag(String branch)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `branch` | `String` | no | `env.BRANCH_NAME` | Branch to resolve. |

## Returns

[`versionResolve`](versionResolve.md)'s value with every character outside
`A-Z a-z 0-9 . _ -` replaced by `-`.

## Examples

With `versionBase()` = `1.4.0`, `BUILD_NUMBER` = `42`, short sha `ab12cd3`:

| Branch | `versionResolve` | `versionImageTag` |
|---|---|---|
| `main` | `1.4.0` | `1.4.0` |
| `release/1.4` | `1.4.0-rc.42` | `1.4.0-rc.42` |
| `hotfix/login` | `1.4.0-hotfix.42.gab12cd3` | `1.4.0-hotfix.42.gab12cd3` |
| `feature/Add_Login` | `1.4.0-feature-add-login.42.gab12cd3` | `1.4.0-feature-add-login.42.gab12cd3` |

```groovy
def tag = versionImageTag()                         // → '1.4.0'
def ref = "${cfg.imageRepo}:${versionImageTag()}"   // → 'ghcr.io/acme/orders-api:1.4.0'
```

## How it fits

[initPipeline](../ci-cd/initPipeline.md) stores it as `env.IMAGE_TAG`, which
image build, scan, sign and GitOps steps use.

## Source

[`vars/versionImageTag.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/versionImageTag.groovy)
