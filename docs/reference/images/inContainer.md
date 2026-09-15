# inContainer

Runs a body inside a container image as the agent's user, with the two fixes
stock images need under `docker.inside`:

- `--entrypoint=''`, so images whose entrypoint is the tool itself
  (`hashicorp/terraform`, `amazon/aws-cli`, `aquasec/trivy`) do not exit at once.
- `HOME` (and `XDG_CACHE_HOME`, `GRADLE_USER_HOME`, `npm_config_cache`) pointed
  at `<workspace>@tmp/home`, because the agent uid has no writable home in the
  image and go, npm, pip, gradle and trivy fail creating their caches.

## Syntax

```groovy
inContainer(String image, String cacheDir) {
    // steps
}
```

## Parameters

| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `image` | `String` | yes | — | Container image to run in. |
| `cacheDir` | `String` | yes (may be `null`) | — | Informational label for the tool's cache; tools choose their own paths under `HOME`. |
| `body` | `Closure` | yes | — | Steps to run inside the container. |

## Returns

Whatever the body returns.

## Examples

```groovy
inContainer('golang:1.27', '.gocache') {
    sh 'go build ./...'
}
// docker run --entrypoint='' ... golang:1.27, HOME=<workspace>@tmp/home
```

```groovy
def digest = inContainer('gcr.io/go-containerregistry/crane:debug', null) {
    sh(script: 'crane digest ghcr.io/acme/api:1.4.0', returnStdout: true).trim()
}
```

Caches live in the workspace's `@tmp` directory, so they last for the build,
not across builds.

## How it fits

Called by [inBuildContainer](inBuildContainer.md) and
[inToolContainer](inToolContainer.md) when not running on a toolbox agent.
Requires the Docker Pipeline plugin and a docker socket.

## Source

[`vars/inContainer.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/inContainer.groovy)
