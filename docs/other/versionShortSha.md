# versionShortSha

Short commit sha. Reads `GIT_COMMIT` when Jenkins provides it to avoid a
subprocess on every call.

## Signature

```groovy
def call(int len = 7)
```

## Parameters

| Name | Type | Description |
|---|---|---|
| `len` | `int` | Number of characters to keep (default 7). |

## Returns

The first `len` characters of the commit sha.

## Usage

```groovy
def sha = versionShortSha()
```

Used by [`versionResolve`](versionResolve.md) and directly by steps that
need a short sha in a resource name (e.g. [`assumeAwsRole`](../cloud/assumeAwsRole.md)'s
session name).

## Source

[`vars/versionShortSha.groovy`](https://github.com/platform-pipelines/platform-pipeline-library/blob/main/vars/versionShortSha.groovy)
