# nodeInstall

Installs Node dependencies with `npm ci`, not `npm install`: it honours the
lockfile exactly and fails loudly if `package.json` and the lockfile
disagree, instead of silently rewriting the lockfile mid-build.

## Syntax

```groovy
nodeInstall(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config; unused, kept for a consistent signature. |

## Returns

Nothing. Fails the build if `package-lock.json` is missing or out of sync.

## Examples

```groovy
nodeInstall(cfg)
```

Runs:

```bash
npm ci --prefer-offline --no-audit --fund=false
```

Sample failure when the lockfile is stale:

```
npm ERR! `npm ci` can only install packages when your package.json and package-lock.json are in sync.
npm ERR! Missing: zod@3.24.1 from lock file
```

Fix locally with `npm install` and commit the updated lockfile.

## How it fits

Called by [nodeBuild](nodeBuild.md) and [nodeLint](nodeLint.md).

## Source

[`vars/nodeInstall.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/nodeInstall.groovy)
