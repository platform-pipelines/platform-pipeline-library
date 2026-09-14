# nodeInstall

Installs Node dependencies with `npm ci`, not `npm install`: it honours the
lockfile exactly and fails loudly if `package.json` and the lockfile
disagree, instead of silently rewriting the lockfile mid-build.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config (unused directly; kept for dispatch signature parity). |

## Returns

Nothing — runs `npm ci --prefer-offline --no-audit --fund=false`.

## Usage

```groovy
nodeInstall(cfg)
```

Called by [nodeBuild](nodeBuild.md) and [nodeLint](nodeLint.md).

## Source

[`vars/nodeInstall.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/nodeInstall.groovy)
