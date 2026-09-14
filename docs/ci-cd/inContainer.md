# inContainer

Runs a body inside a container image, with a named volume at `/cache` so
dependency downloads survive between builds.

## Signature

```groovy
def call(String image, String cacheDir, Closure body)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `image` | `String` | Container image to run in. |
| `cacheDir` | `String` | Cache directory name, mounted at `/cache` when non-empty. |
| `body` | `Closure` | Steps to run inside the container. |

## Returns

Nothing — runs `body()` inside `docker.image(image).inside(...)`.

## Usage

```groovy
inContainer('golang:1.23', '.gocache') { sh 'go build ./...' }
```

Called by [inBuildContainer](inBuildContainer.md) when not running on a
toolbox agent.

## Source

[`vars/inContainer.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/inContainer.groovy)
