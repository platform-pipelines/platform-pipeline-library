# configMerge

Recursive map merge, right side wins.

Lists replace rather than concatenate: when someone writes
`trivyFailOn: [CRITICAL]` they mean "only critical", not "critical as well as
the defaults".

!!! warning "Closure recursion gotcha"
    The implementation calls `this.call(out[k], v)` rather than a bare
    `call(out[k], v)` for the recursive merge. Inside the `.each` closure, a
    bare `call(...)` resolves to `Closure.call()` (invoking the closure
    itself, silently rebinding `out[k]`/`v` to its own `k`/`v` params)
    instead of recursing into this method — a subtle bug that previously
    wiped sibling config keys on every nested merge. `this.call(...)` is
    required.

## Signature

```groovy
def call(Map left, Map right)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `left` | `Map` | Base map (loses on conflicts). |
| `right` | `Map` | Overriding map (wins on conflicts); nested `Map`s merge recursively, other values replace. |

## Returns

A new `Map`; `left` and `right` are not mutated.

## Usage

```groovy
def merged = configMerge(configDefaults(), rawYamlConfig)
```

Marked `@NonCPS`: it's pure data transformation with no pipeline steps, and
recursion under Jenkins's CPS transform is far more expensive — and more
prone to stack issues — than a plain recursive call.

Used throughout [`configLoad`](configLoad.md) to layer defaults, YAML config,
and inline overrides.

## Source

[`vars/configMerge.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configMerge.groovy)
