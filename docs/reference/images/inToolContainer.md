# inToolContainer

Runs a scanner body in the right place for this agent: directly on a toolbox
agent, or inside the scanner's own image otherwise.

## Syntax

```groovy
inToolContainer(String image) {
    // scanner steps
}
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `image` | `String` | yes | — | Scanner image to use when the agent isn't the toolbox. |
| `body` | `Closure` | yes | — | Scanner steps to run. |

## Returns

The value of `body` — so a `return sh(..., returnStatus: true)` inside it
comes back to the caller.

| Agent | Where `body` runs |
|---|---|
| `CI_TOOLBOX=true` | directly on the agent |
| any other | `docker.image(image).inside("--entrypoint=''")` |

## Examples

```groovy
inToolContainer('aquasec/trivy:latest') {
    sh 'trivy fs --severity CRITICAL --exit-code 1 .'
}
```

Capturing a result:

```groovy
def status = inToolContainer('zricethezav/gitleaks:latest') {
    return sh(script: 'gitleaks detect --source . --no-banner', returnStatus: true)
}
if (status != 0) { unstable 'possible secrets' }
```

Images the library uses:

| Step | Image |
|---|---|
| [scanTrivy](../quality/scanTrivy.md), [scanIac](../quality/scanIac.md), [generateSbom](generateSbom.md) | `aquasec/trivy:latest` |
| [scanSecrets](../quality/scanSecrets.md) | `zricethezav/gitleaks:latest` |
| [scanSonar](../quality/scanSonar.md) | `sonarsource/sonar-scanner-cli:latest` |
| [argoSync](../deploy/argoSync.md) | `quay.io/argoproj/argocd:latest` |

## How it fits

Checks [usingToolbox](usingToolbox.md) first.

## Source

[`vars/inToolContainer.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/inToolContainer.groovy)
