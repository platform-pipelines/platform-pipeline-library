# isInfraRepo

True when this repo describes infrastructure rather than an application.

## Signature

```groovy
def call(Map cfg)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `cfg` | `Map` | Pipeline config; only `cfg.buildTool` is read. |

## Returns

`true` if `cfg.buildTool` is one of the infra tools returned by
[configInfraTools](configInfraTools.md) (`terraform`, `cloudformation`).

## Usage

```groovy
if (isInfraRepo(cfg)) { ... }
```

## Source

[`vars/isInfraRepo.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/isInfraRepo.groovy)
