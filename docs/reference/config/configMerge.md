# configMerge

Recursive map merge, right side wins.

Lists replace rather than concatenate: when someone writes
`trivyFailOn: [CRITICAL]` they mean "only critical", not "critical as well as
the defaults". A `null` on the right never overwrites a value on the left.

!!! warning "Closure recursion gotcha"
    The implementation calls `this.call(out[k], v)` rather than a bare
    `call(out[k], v)` for the recursive merge. Inside the `.each` closure, a
    bare `call(...)` resolves to `Closure.call()` (invoking the closure
    itself, silently rebinding `out[k]`/`v` to its own `k`/`v` params)
    instead of recursing into this method — a subtle bug that previously
    wiped sibling config keys on every nested merge. `this.call(...)` is
    required.

## Syntax

```groovy
configMerge(Map left, Map right)
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `left` | `Map` | yes | — | Base map (loses on conflicts). |
| `right` | `Map` | yes | — | Overriding map (wins on conflicts). |

Merge rules for each key in `right`:

| `left[k]` | `right[k]` | Result |
|---|---|---|
| `Map` | `Map` | merged recursively |
| anything | list / string / number / boolean | `right[k]` replaces it |
| anything | `null` | `left[k]` is kept |
| missing | anything non-null | `right[k]` is added |

## Returns

A new `Map`; `left` and `right` are not mutated.

## Examples

```groovy
def defaults = [quality: [sonar: true, trivy: true, trivyFailOn: ['HIGH', 'CRITICAL']], dockerfile: 'Dockerfile']
def repo     = [quality: [trivyFailOn: ['CRITICAL'], sonar: null], dockerfile: 'docker/Dockerfile']

configMerge(defaults, repo)
// → [quality: [sonar: true, trivy: true, trivyFailOn: ['CRITICAL']],
//    dockerfile: 'docker/Dockerfile']
```

Layering three sources, as [`configLoad`](configLoad.md) does:

```groovy
def cfg = configMerge(configMerge(configDefaults(), readYaml(file: '.ci/config.yaml')),
                      [quality: [minCoverage: 90]])
```

## How it fits

Marked `@NonCPS`: it's pure data transformation with no pipeline steps, and
recursion under Jenkins's CPS transform is far more expensive — and more
prone to stack issues — than a plain recursive call.

Used throughout [`configLoad`](configLoad.md) to layer defaults, YAML config,
and inline overrides.

## Source

[`vars/configMerge.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configMerge.groovy)
