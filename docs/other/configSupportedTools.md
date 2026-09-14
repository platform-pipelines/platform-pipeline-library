# configSupportedTools

Single source of truth for legal `buildTool` values. Adding a language or an
IaC type means adding it here and registering its lint/build/test steps.

## Signature

```groovy
def call()
```

## Returns

`List` of every `buildTool` value [`configValidate`](configValidate.md)
accepts: application languages (`maven`, `gradle`, `npm`, `python`, `go`,
`docker-only`) plus infrastructure-as-code types (`terraform`,
`cloudformation`).

## Usage

```groovy
def tools = configSupportedTools()
```

See [`configInfraTools`](../cloud/configInfraTools.md) for the subset of
these that are infrastructure rather than an application.

## Source

[`vars/configSupportedTools.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configSupportedTools.groovy)
