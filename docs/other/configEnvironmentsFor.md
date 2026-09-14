# configEnvironmentsFor

Which environments the given branch is permitted to reach.

## Signature

```groovy
def call(Map cfg, String branch)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; reads `cfg.environments`. |
| `branch` | `String` | Branch name to match against each environment's `branchPattern`. |

## Returns

`List` of environment config `Map`s the branch is permitted to reach (may be
empty).

## Usage

```groovy
def envs = configEnvironmentsFor(cfg, env.BRANCH_NAME)
```

Matches each environment's `branchPattern` by converting it to a regex via
[`configGlobToRegex`](configGlobToRegex.md). A null/empty `branch` reaches no
environments rather than throwing.

## Source

[`vars/configEnvironmentsFor.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configEnvironmentsFor.groovy)
