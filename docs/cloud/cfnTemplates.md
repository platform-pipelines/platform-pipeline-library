# cfnTemplates

Resolves the template files this repo owns. Explicit config wins; otherwise
globs the working directory. Nested stacks referenced from a parent are
linted through the parent, so a flat glob is the right default.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.infra.templates`/`workingDir`. |

## Returns

A `List` of template file paths — either `cfg.infra.templates` verbatim, or a
glob of `*.yaml`/`*.yml`/`*.json` under `cfg.infra.workingDir` (max depth 2).

## Usage

```groovy
def templates = cfnTemplates(cfg)
```

Used by [cfnBuild](cfnBuild.md) and [cfnLint](cfnLint.md).

## Source

[`vars/cfnTemplates.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/cfnTemplates.groovy)
