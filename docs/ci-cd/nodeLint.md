# nodeLint

ESLint, Prettier, and a TypeScript typecheck when `tsconfig.json` is present.

!!! note "Type errors always fail"
    TypeScript type errors fail the build regardless of `cfg.lint.failOnError`
    — shipping a build that doesn't typecheck isn't a style preference.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.lint.autoFormat` and `cfg.lint.failOnError`. |

## Returns

Nothing — writes `eslint-report.xml` (converted from ESLint's JSON output via
a bundled [useScript](../other/useScript.md) helper), and errors if ESLint,
Prettier, or `tsc` report problems (subject to `failOnError` for ESLint and
Prettier only).

## Usage

```groovy
nodeLint(cfg)
```

Called by [lintApp](lintApp.md) when `cfg.buildTool == 'npm'`. Installs
dependencies via [nodeInstall](nodeInstall.md) first.

## Source

[`vars/nodeLint.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/nodeLint.groovy)
