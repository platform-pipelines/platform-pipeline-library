# cfnTemplates

Resolves the template files this repo owns. Explicit config wins; otherwise
globs the working directory. Nested stacks referenced from a parent are
linted through the parent, so a flat glob is the right default.

## Syntax

```groovy
cfnTemplates(Map cfg)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `cfg` | `Map` | yes | — | Pipeline config. |

### Config keys read

| Key | Default | Sample value | Effect |
|---|---|---|---|
| `infra.templates` | `null` | `[templates/root.yaml, templates/network.yaml]` | When set, returned as-is. |
| `infra.workingDir` | `.` | `templates` | Searched (max depth 2) when `infra.templates` is unset. |

## Returns

A `List<String>` of template paths, sorted. The glob matches `*.yaml`,
`*.yml` and `*.json`, skipping hidden paths. Empty list when nothing matches.

!!! warning "The glob is broad"
    Every YAML/JSON file under `workingDir` counts as a template — including
    parameter files or unrelated config. If the directory holds more than
    templates, set `infra.templates` explicitly.

## Examples

Glob mode:

```
templates/
├── root.yaml
├── network.yaml
└── params/
    └── prod.json
```

```yaml
infra:
  workingDir: templates
```

```groovy
cfnTemplates(cfg)
// → ['templates/network.yaml', 'templates/params/prod.json', 'templates/root.yaml']
```

Explicit mode — only the listed files:

```yaml
infra:
  templates:
    - templates/root.yaml
    - templates/network.yaml
```

```groovy
cfnTemplates(cfg)   // → ['templates/root.yaml', 'templates/network.yaml']
```

## How it fits

Used by [cfnBuild](cfnBuild.md) and [cfnLint](cfnLint.md).

## Source

[`vars/cfnTemplates.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/cfnTemplates.groovy)
