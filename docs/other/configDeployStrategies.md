# configDeployStrategies

Single source of truth for legal `deployStrategy` values.

## Signature

```groovy
def call()
```

## Returns

`List` — `['gitops', 'terraform', 'cloudformation']`.

## Usage

```groovy
def strategies = configDeployStrategies()
```

Read by [`configValidate`](configValidate.md) and by
[deployToEnvironment](../cloud/deployToEnvironment.md)'s error message. Adding
a strategy means adding it here and adding a case to `deployToEnvironment`.

## Source

[`vars/configDeployStrategies.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/configDeployStrategies.groovy)
