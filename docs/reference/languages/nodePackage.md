# nodePackage

Stamps `package.json` with the resolved version and produces an `npm pack`
tarball.

## Syntax

```groovy
nodePackage(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; unused, kept so every `*Package` step has the same signature. |

Reads `env.APP_VERSION`.

## Returns

Nothing. Writes `<name>-<version>.tgz` in the workspace root, which
[appArtifacts](../pipelines/appArtifacts.md) (`*.tgz`) archives and
[publishArtifact](../deploy/publishArtifact.md) uploads.

## Examples

```groovy
nodePackage(cfg)
```

Runs (with `APP_VERSION=1.4.0`):

```bash
npm version 1.4.0 --no-git-tag-version --allow-same-version
npm pack
```

For `"name": "checkout-api"` this produces `checkout-api-1.4.0.tgz`; for a
scoped `"name": "@acme/checkout-api"` it produces `acme-checkout-api-1.4.0.tgz`.

`npm version` needs valid semver, which every
[versionResolve](../utilities/versionResolve.md) shape is
(e.g. `1.4.0-feature-login.42.gab12cd3`).

## How it fits

Called by [packageApp](../pipelines/packageApp.md) when `cfg.buildTool == 'npm'`.

## Source

[`vars/nodePackage.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/nodePackage.groovy)
