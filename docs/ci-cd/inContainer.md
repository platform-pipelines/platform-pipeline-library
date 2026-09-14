# inContainer

Runs a body inside a container image, with a named volume at `/cache` so
dependency downloads survive between builds.

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
| `cacheDir` | `String` | yes (may be `null`) | — | Non-empty → mount a cache volume; `null`/`''` → no cache. |
| `body` | `Closure` | yes | — | Steps to run inside the container. |

## Returns

Nothing. When `cacheDir` is set, the container gets:

| Item | Value |
|---|---|
| Volume | `ci-cache-<image with non-alphanumerics replaced by ->` mounted at `/cache` |
| Env var | `CACHE_DIR=/cache` |

The volume name depends only on the image, not on `cacheDir`, so every build
using the same image shares one cache.

## Examples

```groovy
inContainer('golang:1.27', '.gocache') {
    sh 'GOMODCACHE=$CACHE_DIR/mod go build ./...'
}
// docker run ... -v ci-cache-golang-1-27:/cache -e CACHE_DIR=/cache golang:1.27
```

```groovy
inContainer('alpine:3.19', null) {
    sh 'apk add --no-cache jq && jq --version'
}
// no cache volume
```

Point the tool's cache at `$CACHE_DIR` to actually use the volume, e.g.
`pip install --cache-dir $CACHE_DIR/pip`, `npm ci --cache $CACHE_DIR/npm`.

## How it fits

Called by [inBuildContainer](inBuildContainer.md) when not running on a
toolbox agent. Requires the Docker Pipeline plugin and a docker socket.

## Source

[`vars/inContainer.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/inContainer.groovy)
